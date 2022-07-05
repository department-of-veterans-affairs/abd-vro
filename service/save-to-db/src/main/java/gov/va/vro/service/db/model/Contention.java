package gov.va.vro.service.db.model;

import lombok.Data;

import java.util.List;

@Data
public class Contention {

  private String diagnosticCode;
  private List<AssessmentResult> assessmentResults;
  private List<EvidenceSummaryDocument> evidenceSummaryDocuments;
}
