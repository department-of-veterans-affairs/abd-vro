package gov.va.vro.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AssessmentResult {

  private int evidenceCount;
  private String evidenceSummary;
}
