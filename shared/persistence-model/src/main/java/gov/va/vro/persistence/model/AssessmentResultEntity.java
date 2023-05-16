package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  private Boolean sufficientEvidenceFlag;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, String> evidenceCountSummary;
}
