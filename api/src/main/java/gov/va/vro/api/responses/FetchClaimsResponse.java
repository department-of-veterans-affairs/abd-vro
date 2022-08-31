package gov.va.vro.api.responses;

import gov.va.vro.api.model.ClaimInfo;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class FetchClaimsResponse {
  private List<ClaimInfo> claims;
  private String errorMessage;
}
