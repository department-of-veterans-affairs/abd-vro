package gov.va.vro.model.rrd.claimmetrics;

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
public class ContentionInfo {
  private String diagnosticCode;

  private List<AssessmentInfo> assessments;

  private List<DocumentInfo> documents;
}
