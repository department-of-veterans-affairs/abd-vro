package gov.va.vro.api.cc;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceException extends Exception {

  @Getter private final String resourceId;
  @Getter private final HttpStatus httpStatus;

  /**
   * Wraps around exception for sending back to API client.
   *
   * @param resourceId id
   * @param httpStatus return status code
   * @param exception to be wrapped
   */
  public ResourceException(String resourceId, HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.resourceId = resourceId;
    this.httpStatus = httpStatus;
  }
}
