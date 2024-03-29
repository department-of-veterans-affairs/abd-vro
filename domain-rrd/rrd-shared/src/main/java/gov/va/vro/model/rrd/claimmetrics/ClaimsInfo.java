package gov.va.vro.model.rrd.claimmetrics;

import gov.va.vro.model.rrd.claimmetrics.response.ClaimInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClaimsInfo {
  private List<ClaimInfoResponse> claimInfoList;
  private long total;
}
