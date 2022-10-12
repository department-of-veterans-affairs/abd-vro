package gov.va.vro.abddataaccess.exception;

/**
 * ABD Exception.
 *
 * @author Warren Lin
 */
public class AbdException extends Exception {
  private static final String ABD_ERROR = "ABD error.";

  public AbdException() {
    super(ABD_ERROR);
  }

  public AbdException(String msg) {
    super(msg);
  }

  public AbdException(String message, Throwable cause) {
    super(message, cause);
  }

  public AbdException(Throwable cause) {
    super(ABD_ERROR, cause);
  }
}
