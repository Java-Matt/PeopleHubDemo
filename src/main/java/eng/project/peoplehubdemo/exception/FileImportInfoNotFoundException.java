package eng.project.peoplehubdemo.exception;

public class FileImportInfoNotFoundException extends RuntimeException{
    public FileImportInfoNotFoundException() {
    }

    public FileImportInfoNotFoundException(String message) {
        super(message);
    }
}
