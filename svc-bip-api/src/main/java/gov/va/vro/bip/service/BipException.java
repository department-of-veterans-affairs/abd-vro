package gov.va.vro.bip.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Custom Bip Exception.
 *
 * @author warren date 10/31/22
 */
@Getter
@Setter
public class BipException extends RuntimeException {

  private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

  public BipException(String message) {
    super(message);
  }

  public BipException(String message, Throwable cause) {
    super(message, cause);
  }

  public BipException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }
}
