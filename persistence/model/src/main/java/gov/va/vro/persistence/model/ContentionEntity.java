package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  @ManyToOne private ClaimSubmissionEntity claimSubmission;

  @NotNull private String diagnosticCode;

  @OneToMany(
      mappedBy = "contention",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<AssessmentResultEntity> assessmentResults = new ArrayList<>();

  @OneToMany(mappedBy = "contention", cascade = CascadeType.ALL, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.FALSE)
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

  /**
   * Adds assessment result to a claim.
   *
   * @param ar assessment result entity
   * @return returns the assessment result
   */
  public AssessmentResultEntity addAssessmentResult(AssessmentResultEntity ar) {
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setContention(this);
    assessmentResult.setEvidenceCountSummary(ar.getEvidenceCountSummary());
    assessmentResults.add(assessmentResult);
    return assessmentResult;
  }

  /**
   * Adds evidence summary document to contention entity.
   *
   * @param request evidence summary document entity.
   * @return returns the evidence summary document.
   */
  public EvidenceSummaryDocumentEntity addEvidenceSummaryDocument(
      EvidenceSummaryDocumentEntity request) {
    EvidenceSummaryDocumentEntity document = new EvidenceSummaryDocumentEntity();
    document.setDocumentName(request.getDocumentName());
    document.setEvidenceCount(request.getEvidenceCount());
    document.setContention(this);
    evidenceSummaryDocuments.add(document);
    return document;
  }

  /**
   * Add evidence summary document to contention entity.
   *
   * @param evidenceCount evidence counts
   * @param documentName document name
   */
  public void addEvidenceSummaryDocument(Map<String, String> evidenceCount, String documentName) {
    EvidenceSummaryDocumentEntity esd = new EvidenceSummaryDocumentEntity();
    esd.setEvidenceCount(evidenceCount);
    esd.setDocumentName(documentName);
    esd.setContention(this);
    evidenceSummaryDocuments.add(esd);
  }
}
