package gov.va.vro.model.claimmetrics.response;

import gov.va.vro.model.claimmetrics.ContentionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ClaimInfoResponse {
  private String claimSubmissionId;
  private String veteranIcn;
  private List<ContentionInfo> contentions;
}
