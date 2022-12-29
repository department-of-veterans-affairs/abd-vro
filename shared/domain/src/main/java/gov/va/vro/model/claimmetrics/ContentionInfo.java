package gov.va.vro.model.claimmetrics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ContentionInfo {
  private String diagnosticCode;

  private List<AssessmentInfo> assessments;

  private List<DocumentInfo> documents;
}
