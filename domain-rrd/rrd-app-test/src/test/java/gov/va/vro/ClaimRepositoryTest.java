package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClaimRepositoryTest extends BaseIntegrationTest {

  @Test
  void insertQuery() {
    Date icnTimestamp = new Date();

    var veteran = TestDataSupplier.createVeteran("X", "Y", icnTimestamp);
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    assertNotNull(veteran.getIcnTimestamp());
    assertNotNull(veteran.getCreatedAt());
    assertNotNull(veteran.getUpdatedAt());

    EvidenceSummaryDocumentEntity evidenceSummaryDocument1 = new EvidenceSummaryDocumentEntity();
    Map<String, String> count1 = new HashMap<>();
    count1.put("count", "1");
    evidenceSummaryDocument1.setEvidenceCount(count1);
    evidenceSummaryDocument1.setDocumentName("documentName1");
    EvidenceSummaryDocumentEntity evidenceSummaryDocument2 = new EvidenceSummaryDocumentEntity();
    Map<String, String> count2 = new HashMap<>();
    count2.put("count2", "2");
    evidenceSummaryDocument2.setEvidenceCount(count2);
    evidenceSummaryDocument2.setDocumentName("documentName2");
    Map<String, String> evidence = new HashMap<>();
    evidence.put("medicationsCount", "10");
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setEvidenceCountSummary(evidence);
    ContentionEntity contention1 = new ContentionEntity("c1");
    contention1.addAssessmentResult(assessmentResult);
    contention1.addEvidenceSummaryDocument(evidenceSummaryDocument1);
    contention1.addEvidenceSummaryDocument(evidenceSummaryDocument2);

    ContentionEntity contention2 = new ContentionEntity("c2");
    Map<String, String> evidence2 = new HashMap<>();
    AssessmentResultEntity ar2 = new AssessmentResultEntity();
    evidence2.put("medicationsCount", "10");
    ar2.setEvidenceCountSummary(evidence2);
    contention2.addAssessmentResult(ar2);

    var claim = TestDataSupplier.createClaim("123", veteran, "refId");
    claim.addContention(contention1);
    claim.addContention(contention2);

    claim = claimRepository.save(claim);
    assertNotNull(claim.getId());
    assertNotNull(claim.getCreatedAt());

    List<ContentionEntity> contentions = claim.getContentions();
    assertEquals(2, contentions.size());
    contentions.forEach(contention -> assertNotNull(contention.getId()));
    contentions.stream()
        .filter(contention -> "c1".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention -> assertEquals(2, contention1.getEvidenceSummaryDocuments().size()),
            Assertions::fail);
    contentions.stream()
        .filter(contention -> "c1".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention ->
                assertEquals(
                    evidence, contention1.getAssessmentResults().get(0).getEvidenceCountSummary()),
            Assertions::fail);
    contentions.stream()
        .filter(contention -> "c2".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention -> assertEquals(1, contention2.getAssessmentResults().size()),
            Assertions::fail);
    contentions.stream()
        .filter(contention -> "c2".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention ->
                assertEquals(
                    evidence2, contention2.getAssessmentResults().get(0).getEvidenceCountSummary()),
            Assertions::fail);
    assertEquals(
        contention1.getEvidenceSummaryDocuments().get(0).getDocumentName(), "documentName1");
    assertEquals(contention1.getEvidenceSummaryDocuments().get(0).getEvidenceCount().size(), 1);
    assertEquals(contention1.getEvidenceSummaryDocuments().get(0).getEvidenceCount(), count1);
    assertEquals(
        contention1.getEvidenceSummaryDocuments().get(1).getDocumentName(), "documentName2");
    assertEquals(contention1.getEvidenceSummaryDocuments().get(1).getEvidenceCount().size(), 1);
    assertEquals(contention1.getEvidenceSummaryDocuments().get(1).getEvidenceCount(), count2);
  }
}
