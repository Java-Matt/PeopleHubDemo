package eng.project.peoplehubdemo.exception;

public class ExperienceOverlappingException extends RuntimeException{
    public ExperienceOverlappingException() {
    }

    public ExperienceOverlappingException(String message) {
        super(message);
    }
}
