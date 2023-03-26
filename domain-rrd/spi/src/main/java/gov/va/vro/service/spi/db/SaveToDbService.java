package gov.va.vro.service.spi.db;

import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.ExamOrder;
import gov.va.vro.service.spi.model.GeneratePdfPayload;

import java.util.List;
import java.util.UUID;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);

  void insertAssessmentResult(UUID claimId, AbdEvidenceWithSummary evidence, String diagnosticCode);

  void insertAssessmentResult(AbdEvidenceWithSummary evidence, String diagnosticCode);

  void insertEvidenceSummaryDocument(GeneratePdfPayload request, String documentName);

  void updateEvidenceSummaryDocument(UUID eFolderId, MasAutomatedClaimPayload payload);

  void insertOrUpdateExamOrderingStatus(ExamOrder examOrder);

  void insertFlashIds(List<String> veteranFlashIds, String veteranIcn);

  void updateRfdFlag(String claimId, boolean rfdFlag);

  void setOffRampReason(Claim payload);

  void updateSufficientEvidenceFlag(AbdEvidenceWithSummary evidence, String diagnosticCode);
}
