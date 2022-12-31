package gov.va.vro.model.claimmetrics;

import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClaimsInfo {
  private List<ClaimInfoResponse> claimInfoList;
  private long total;
}
