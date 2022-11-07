package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClaimRepositoryTest extends BaseIntegrationTest {

  @Test
  void insertQuery() {
    var veteran = TestDataSupplier.createVeteran("X", "Y");
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    assertNotNull(veteran.getCreatedAt());
    assertNotNull(veteran.getUpdatedAt());

    ContentionEntity contention1 = new ContentionEntity("c1");
    AssessmentResultEntity ar = new AssessmentResultEntity();
    EvidenceSummaryDocumentEntity ev1 = new EvidenceSummaryDocumentEntity();
    EvidenceSummaryDocumentEntity ev2 = new EvidenceSummaryDocumentEntity();
    Map<String, String> count = new HashMap<>();
    count.put("count", "1");
    ev1.setEvidenceCount(count);
    count.put("count2", "2");
    ev2.setEvidenceCount(count);
    Map<String, String> evidence = new HashMap<>();
    evidence.put("medicationsCount", "10");
    ar.setEvidenceCountSummary(evidence);
    contention1.addAssessmentResult(ar);
    contention1.addEvidenceSummaryDocument(ev1);
    contention1.addEvidenceSummaryDocument(ev2);

    ContentionEntity contention2 = new ContentionEntity("c2");
    Map<String, String> evidence2 = new HashMap<>();
    AssessmentResultEntity ar2 = new AssessmentResultEntity();
    evidence2.put("medicationsCount", "10");
    ar2.setEvidenceCountSummary(evidence2);
    contention2.addAssessmentResult(ar2);

    var claim = TestDataSupplier.createClaim("123", "type", veteran);
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
  }
}
