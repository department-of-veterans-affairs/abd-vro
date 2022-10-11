package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {

  // number of evidence data points found to support fast tracking the claim
  @Min(0)
  private int evidenceCount;

  private Map<String, Object> evidenceCountSummary;

  @JsonAnySetter
  void setEvidenceCountSummary(String key, Object value) {
    evidenceCountSummary.put(key, value);
  }
}
