package eng.project.peoplehubdemo.exception;

public class FileInfoWithFileNumberAlreadyExistsException extends RuntimeException{
    public FileInfoWithFileNumberAlreadyExistsException() {
    }

    public FileInfoWithFileNumberAlreadyExistsException(String message) {
        super(message);
    }
}
