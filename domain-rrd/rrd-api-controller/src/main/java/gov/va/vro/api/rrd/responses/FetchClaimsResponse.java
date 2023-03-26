package gov.va.vro.api.rrd.responses;

import gov.va.vro.api.rrd.model.ClaimInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
