package gov.va.vro.service.provider.mas;

/**
 * Exception thrown in MAS API service.
 *
 * @author warren @Date 10/5/22
 */
public class MasException extends RuntimeException {

  public MasException(String message, Throwable cause) {
    super(message, cause);
  }

  public MasException(String message) {
    super(message);
  }
}
