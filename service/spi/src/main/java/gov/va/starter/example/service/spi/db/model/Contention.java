package gov.va.starter.example.service.spi.db.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Contention {

  private String diagnosticCode;
  private List<AssessmentResult> assessmentResults = new ArrayList<>();
  private List<EvidenceSummaryDocument> evidenceSummaryDocuments = new ArrayList<>();

  public AssessmentResult addAssessmentResult(int evidenceCount) {
    AssessmentResult assessmentResult = new AssessmentResult();
    assessmentResult.setEvidenceCount(evidenceCount);
    assessmentResults.add(assessmentResult);
    return assessmentResult;
  }

  public EvidenceSummaryDocument addEvidenceSummaryDocument(
      String documentName, int evidenceCount) {
    EvidenceSummaryDocument document = new EvidenceSummaryDocument();
    document.setDocumentName(documentName);
    document.setEvidenceCount(evidenceCount);
    evidenceSummaryDocuments.add(document);
    return document;
  }
}
