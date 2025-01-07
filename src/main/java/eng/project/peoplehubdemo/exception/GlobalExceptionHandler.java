package eng.project.peoplehubdemo.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PersonNotFoundException.class, FileImportInfoNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNotFoundException(RuntimeException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler({ExperienceOverlappingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleBadRequestException(RuntimeException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler({FileLoadingFailureException.class, ClassImportException.class,
            FileInfoWithFileNumberAlreadyExistsException.class, DataAlreadyModifiedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleInternalServerError(RuntimeException exception) {
        return new ExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorDto handleValidationExceptions(MethodArgumentNotValidException exception) {
        ValidationErrorDto errorDto = new ValidationErrorDto();
        exception.getFieldErrors().forEach(fieldError ->
                errorDto.addViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        return errorDto;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleConstraintViolationExceptions(ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations().iterator().next().getMessageTemplate();
        return new ExceptionDto(errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessDeniedException(AccessDeniedException exception) {
        return new ExceptionDto(MessageFormat.format("ACCESS DENIED {0}", exception.getCause()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String message = Objects.requireNonNull(exception.getRootCause()).getLocalizedMessage();
        return new ExceptionDto(extractMessageFromError(message));
    }

    private String extractMessageFromError(String message) {
        Pattern pattern = Pattern.compile("Duplicate entry '(.+?)' for key '(\\w+?)\\.\\w+?_([^']+)'");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String duplicatedValue = matcher.group(1);
            String className = matcher.group(2);
            String field = matcher.group(3);
            return MessageFormat.format("{0} with {1}={2} already exists", classNameFormatter(className), fieldNameFormatter(field), duplicatedValue);
        }
        return "Something went wrong sorry please contact your if department!";
    }

    private String classNameFormatter(String className) {
        return className.substring(0, 1).toUpperCase() + className.substring(1).toLowerCase();
    }

    private String fieldNameFormatter(String fieldName) {
        StringBuilder formatted = new StringBuilder();
        String[] parts = fieldName.toLowerCase().split("_");
        formatted.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            formatted.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }
        return formatted.toString();
    }
}
