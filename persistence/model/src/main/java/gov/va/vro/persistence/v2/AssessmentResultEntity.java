package gov.va.vro.persistence.v2;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCountSummary;
}
