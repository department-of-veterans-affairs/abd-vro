package gov.va.vro.service.spi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClaimPayload {
  @NonNull private String veteranIcn;

  @NonNull private String diagnosticCode;

  private String claimSubmissionId;
}
