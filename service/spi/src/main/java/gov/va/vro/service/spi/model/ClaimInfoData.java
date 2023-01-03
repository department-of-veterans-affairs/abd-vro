package gov.va.vro.service.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class ClaimInfoData {
  String claimSubmissionId;
  String veteranIcn;
  int contentionsCount;
  int assessmentResultsCount;
  int evidenceSummaryDocumentsCount;
  List<String> contentions;
  Map<String, String> evidenceSummary;
  String errorMessage;
}
