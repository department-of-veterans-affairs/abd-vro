package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@Table(name = "assessment_result")
public class AssessmentResultEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Min(0)
  private int evidenceCount;

  @OneToMany private List<EvidenceCountSummaryEntity> evidenceCountSummary;

  public EvidenceCountSummaryEntity addEvidenceCountSummary(EvidenceCountSummaryEntity ecs) {
    EvidenceCountSummaryEntity evidenceCountSummary1 = new EvidenceCountSummaryEntity();
    evidenceCountSummary1.setMedicationsCount(ecs.getMedicationsCount());
    evidenceCountSummary1.setRecentBpReadings(ecs.getRecentBpReadings());
    evidenceCountSummary1.setTotalBpReadings(ecs.getTotalBpReadings());
    evidenceCountSummary.add(evidenceCountSummary1);
    return evidenceCountSummary1;
  }
}
