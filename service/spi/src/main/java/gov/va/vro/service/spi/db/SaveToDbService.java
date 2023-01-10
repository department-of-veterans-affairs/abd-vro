package gov.va.vro.service.spi.db;

import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;

import java.util.UUID;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);

  void insertAssessmentResult(String claimSubmissionId, String idType, AbdEvidenceWithSummary evidence, String diagnosticCode);

  void insertEvidenceSummaryDocument(GeneratePdfPayload request, String documentName);
}
