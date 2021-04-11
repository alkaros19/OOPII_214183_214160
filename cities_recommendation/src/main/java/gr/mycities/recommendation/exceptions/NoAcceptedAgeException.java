package gr.mycities.recommendation.exceptions;

public class NoAcceptedAgeException extends Exception {

    /**
     * Creates a new instance of <code>NoAcceptedAgeException</code> without
     * detail message.
     */
    public NoAcceptedAgeException() {
    }

    /**
     * Constructs an instance of <code>NoAcceptedAgeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoAcceptedAgeException(String msg) {
        super(msg);
    }
}
