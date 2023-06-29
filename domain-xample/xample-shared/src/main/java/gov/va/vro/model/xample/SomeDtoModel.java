package gov.va.vro.model.xample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

// @NoArgsConstructor
@AllArgsConstructor
// Use toBuilder as a copy constructor
@Builder(toBuilder = true)
@Data
public class SomeDtoModel {
  @NonNull private String resourceId;

  @NonNull private String diagnosticCode;

  // Used to determine the HTTP status code returned by the API Controller
  private String status;

  @Builder.Default private SomeDtoModelHeader header = new SomeDtoModelHeader();

  // Fluent accessors to make setting multiply fields less verbose
  public SomeDtoModel status(StatusValue status) {
    setStatus(status.name());
    return this;
  }

  public SomeDtoModel status(StatusValue status, String message) {
    setStatus(status.name());
    header.setStatusMessage(message);
    return this;
  }

  public SomeDtoModel header(int statusCode, String message) {
    header.setStatusCode(statusCode);
    header.setStatusMessage(message);
    return this;
  }
}
