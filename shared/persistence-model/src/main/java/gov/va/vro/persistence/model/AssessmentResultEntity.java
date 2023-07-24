package gov.va.vro.persistence.model;

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

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
@TypeDef(name = "json", typeClass = JsonType.class)
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  private Boolean sufficientEvidenceFlag;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCountSummary;
}
