package gov.va.vro.model.xample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
// Use toBuilder as a copy constructor
@Builder(toBuilder = true)
@Data
public class SomeDtoModel {
  @NonNull private String resourceId;

  @NonNull private String diagnosticCode;

  private String status;

  // For interfacing with microservices:
  // Refer to https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
  @Builder.Default private int statusCode = 0;
  // Message to go with the statusCode
  private String statusMessage;

  // Fluent accessors to make setting multiply fields less verbose
  public SomeDtoModel status(StatusValue status) {
    setStatus(status.name());
    return this;
  }

  public SomeDtoModel status(StatusValue status, String message) {
    setStatus(status.name());
    setStatusMessage(message);
    return this;
  }
}
