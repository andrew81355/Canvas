public class CanvasException extends Exception {
    //exception for project to differ our own errors from system
    //first constructor with text of error
    public CanvasException(String message) {
        super(message); //save text in parent Exception
    }
    //second constructor with text of error and reason when error occur bceuase of another error
    public CanvasException(String message, Throwable cause) {
        super(message, cause);
    }
}
