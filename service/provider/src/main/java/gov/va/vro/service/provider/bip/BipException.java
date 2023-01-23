package gov.va.vro.service.provider.bip;

/**
 * Custom Bip Exception.
 *
 * @author warren date 10/31/22
 */
public class BipException extends RuntimeException {

  public BipException(String message) {
    super(message);
  }

  public BipException(String message, Throwable cause) {
    super(message, cause);
  }
}
