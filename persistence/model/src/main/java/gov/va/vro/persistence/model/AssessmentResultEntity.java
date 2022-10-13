package gov.va.vro.persistence.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
@TypeDef(name = "json", typeClass = JsonType.class)
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Min(0)
  private int evidenceCount;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCountSummary;

  @JsonAnySetter
  void setEvidenceCountSummary(String key, String value) {
    evidenceCountSummary.put(key, value);
  }
}
