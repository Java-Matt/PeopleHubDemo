package eng.project.peoplehubdemo.exception;

public class FileLoadingFailureException extends RuntimeException{
    public FileLoadingFailureException() {
    }

    public FileLoadingFailureException(String message) {
        super(message);
    }
}
