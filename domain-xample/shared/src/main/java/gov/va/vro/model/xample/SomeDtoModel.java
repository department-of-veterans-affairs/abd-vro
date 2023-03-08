package gov.va.vro.model.xample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SomeDtoModel {
  @NonNull private String resourceId;

  @NonNull private String diagnosticCode;

  private String status;
  private String reason;

  // Fluent accessors to make setting multiply fields less verbose
  public SomeDtoModel status(StatusValue status) {
    setStatus(status.name());
    return this;
  }

  public SomeDtoModel reason(String someReason) {
    setReason(someReason);
    return this;
  }
}
