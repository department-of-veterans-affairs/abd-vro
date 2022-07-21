package gov.va.vro.abd_data_access.exception;

/**
 * ABD Exception.
 *
 *  @author Warren Lin
 */
public class AbdException extends Exception {
    private static final String LIGHTHOUSE_ERROR = "ABD error.";

    public AbdException() {
        super(LIGHTHOUSE_ERROR);
    }

    public AbdException(String msg) {
        super(msg);
    }

    public AbdException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbdException(Throwable cause) {
        super(LIGHTHOUSE_ERROR, cause);
    }
}
