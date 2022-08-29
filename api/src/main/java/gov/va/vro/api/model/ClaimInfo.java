package gov.va.vro.api.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class ClaimInfo {
  private String claimSubmissionId;
  private String veteranIcn;
  private List<String> contentions;
}
