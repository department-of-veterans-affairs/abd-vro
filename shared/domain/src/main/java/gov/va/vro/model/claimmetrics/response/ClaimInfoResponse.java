package gov.va.vro.model.claimmetrics.response;

import gov.va.vro.model.claimmetrics.ContentionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ClaimInfoResponse {
  private String claimSubmissionId;
  private String idType;
  private String veteranIcn;
  private List<ContentionInfo> contentions;
}
