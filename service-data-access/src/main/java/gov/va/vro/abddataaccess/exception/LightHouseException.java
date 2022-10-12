package gov.va.vro.abddataaccess.exception;

/**
 * Exception thrown in Lighthouse API service.
 *
 * @author warren @Date 10/11/22
 */
public class LightHouseException extends AbdException {
  private static final String LIGHTHOUSE_ERROR = "Lighthouse API error.";

  public LightHouseException() {
    super(LIGHTHOUSE_ERROR);
  }

  public LightHouseException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
