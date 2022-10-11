package gov.va.vro.persistence.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Min(0)
  private int evidenceCount;

  @ElementCollection private Map<String, String> evidenceCountSummary;

  @JsonAnySetter
  void setEvidenceCountSummary(String key, String value) {
    evidenceCountSummary.put(key, value);
  }
}
