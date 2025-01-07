package eng.project.peoplehubdemo.exception;

public class DataAlreadyModifiedException extends RuntimeException{
    public DataAlreadyModifiedException() {
    }

    public DataAlreadyModifiedException(String message) {
        super(message);
    }
}
