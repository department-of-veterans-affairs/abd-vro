package gov.va.vro.api.redo;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceException extends Exception {

  @Getter private final String resourceId;
  @Getter private final HttpStatus httpStatus;

  //  public ResourceException(String resourceId, HttpStatus httpStatus, String message) {
  //    super(message);
  //    this.resourceId = resourceId;
  //    this.httpStatus = httpStatus;
  //  }

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
