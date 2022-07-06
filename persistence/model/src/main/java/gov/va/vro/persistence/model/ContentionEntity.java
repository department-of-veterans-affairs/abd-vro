package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContentionEntity extends BaseEntity {

  @ManyToOne private ClaimEntity claim;

  private String diagnosticCode;

  @OneToMany(
      mappedBy = "contention",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<AssessmentResultEntity> assessmentResults = new ArrayList<>();

  @OneToMany(
      mappedBy = "contention",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<EvidenceSummaryDocumentEntity> evidenceSummaryDocuments = new ArrayList<>();

  public ContentionEntity(String diagnosticCode) {
    this.diagnosticCode = diagnosticCode;
  }

  public AssessmentResultEntity addAssessmentResult(int evidenceCount) {
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setEvidenceCount(evidenceCount);
    assessmentResult.setContention(this);
    assessmentResults.add(assessmentResult);
    return assessmentResult;
  }

  public EvidenceSummaryDocumentEntity addEvidenceSummaryDocument(
      String documentName, int evidenceCount) {
    EvidenceSummaryDocumentEntity document = new EvidenceSummaryDocumentEntity();
    document.setDocumentName(documentName);
    document.setEvidenceCount(evidenceCount);
    document.setContention(this);
    evidenceSummaryDocuments.add(document);
    return document;
  }
}
