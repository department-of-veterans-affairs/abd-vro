package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

  @OneToMany(mappedBy = "contention", cascade = CascadeType.ALL, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<EvidenceSummaryDocumentEntity> evidenceSummaryDocuments = new ArrayList<>();

  private String conditionName;

  private String classificationCode;

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
   * @param assessmentResultEntity assessment result entity
   * @return returns the assessment result
   */
  public AssessmentResultEntity addAssessmentResult(AssessmentResultEntity assessmentResultEntity) {
    assessmentResultEntity.setContention(this);
    assessmentResults.add(assessmentResultEntity);
    return assessmentResultEntity;
  }

  /**
   * Adds evidence summary document to contention entity.
   *
   * @param document evidence summary document entity.
   * @return returns the evidence summary document.
   */
  public EvidenceSummaryDocumentEntity addEvidenceSummaryDocument(
      EvidenceSummaryDocumentEntity document) {
    document.setContention(this);
    evidenceSummaryDocuments.add(document);
    return document;
  }
}
