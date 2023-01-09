package gov.va.vro.service.provider.bip;

/**
 * Custom Bip Exception.
 *
 * @author warren date 10/31/22
 */
public class BipException extends RuntimeException {
  private static final String BIP_ERROR = "VA BIP API access error.";

  public BipException() {
    super(BIP_ERROR);
  }

  public BipException(String message) {
    super(message);
  }

  public BipException(String message, Throwable cause) {
    super(message, cause);
  }
}
