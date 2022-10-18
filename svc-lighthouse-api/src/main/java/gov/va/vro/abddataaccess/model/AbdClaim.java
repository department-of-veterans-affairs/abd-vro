package gov.va.vro.abddataaccess.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AbdClaim {
  private String veteranIcn;
  private String diagnosticCode;
  private String claimSubmissionId;
}
