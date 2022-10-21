package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contention")
public class ContentionEntity extends BaseEntity {

  @ManyToOne private ClaimEntity claim;

  @NotNull private String diagnosticCode;

  @OneToMany(
      mappedBy = "contention",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<AssessmentResultEntity> assessmentResults = new ArrayList<>();

  @OneToMany(
      mappedBy = "contention",
      // fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<EvidenceSummaryDocumentEntity> evidenceSummaryDocuments = new ArrayList<>();

  /***
   * <p>Summary.</p>
   *
   *
   * @param diagnosticCode diagnostic code
   *
   */
  public ContentionEntity(String diagnosticCode) {
    this.diagnosticCode = diagnosticCode;
  }

  /***
   * <p>Summary.</p>
   *
   * @param evidenceCount evidence count
   *
   * @return return value
   */
  public AssessmentResultEntity addAssessmentResult(int evidenceCount) {
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setContention(this);
    assessmentResults.add(assessmentResult);
    return assessmentResult;
  }

  public AssessmentResultEntity addAssessmentResult(AssessmentResultEntity ar) {
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setContention(this);
    assessmentResult.setEvidenceCountSummary(ar.getEvidenceCountSummary());
    assessmentResults.add(assessmentResult);
    return assessmentResult;
  }

  /***
   * <p>Summary.</p>
   *
   * @param documentName document name
   *
   * @param evidenceCount evidence count
   *
   * @return return value
   */
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
