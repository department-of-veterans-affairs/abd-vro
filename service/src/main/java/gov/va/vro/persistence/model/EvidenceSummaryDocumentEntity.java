package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Getter
@Setter
@Entity
@Table(name = "evidence_summary_document")
public class EvidenceSummaryDocumentEntity extends BaseEntity {

  @ManyToOne private ContentionEntity contention;

  // number of evidence data points found to support fast tracking the claim
  @Min(0)
  private int evidenceCount;

  private String documentName;
}
