package gov.va.vro.service.provider.mas;

/**
 * Exception thrown in MAS API service.
 *
 * @author warren @Date 10/5/22
 */
public class MasException extends Exception {
  private static final String MAS_ERROR = "VA MAS API access error.";

  public MasException() {
    super(MAS_ERROR);
  }

  public MasException(String message, Throwable cause) {
    super(message, cause);
  }
}
