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
  public String claimSubmissionId;
  public String veteranIcn;
  List<String> contentions;
}
