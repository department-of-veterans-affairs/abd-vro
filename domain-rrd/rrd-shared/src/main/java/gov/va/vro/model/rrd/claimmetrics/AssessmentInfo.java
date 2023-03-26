package gov.va.vro.model.rrd.claimmetrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AssessmentInfo {
  private Map<String, String> evidenceInfo;
  private Boolean sufficientEvidenceFlag;
}
