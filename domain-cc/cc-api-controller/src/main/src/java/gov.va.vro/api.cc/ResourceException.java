package gov.va.vro.api.cc;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceException extends Exception {

  @Getter private final String ccEndpoint;
  @Getter private final HttpStatus httpStatus;

  /**
   * Wraps around exception for sending back to API client.
   *
   * @param ccEndpoint String
   * @param httpStatus return status code
   * @param exception to be wrapped
   */
  public ResourceException(String ccEndpoint, HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.ccEndpoint = ccEndpoint;
    this.httpStatus = httpStatus;
  }
}
