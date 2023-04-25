package gov.va.vro.service.provider;

public class ExternalCallException extends RuntimeException {

  public ExternalCallException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExternalCallException(String message) {
    super(message);
  }
}
