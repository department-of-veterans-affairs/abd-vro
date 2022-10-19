package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ContentionEntity;
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
    Map<String, String> evidence = new HashMap<>();
    evidence.put("medicationsCount", "10");
    ar.setEvidenceCountSummary(evidence);
    ar.setEvidenceCount(10);
    contention1.addAssessmentResult(ar);
    contention1.addEvidenceSummaryDocument("doc1", 1);
    contention1.addEvidenceSummaryDocument("doc2", 2);

    ContentionEntity contention2 = new ContentionEntity("c2");
    Map<String, String> evidence2 = new HashMap<>();
    AssessmentResultEntity ar2 = new AssessmentResultEntity();
    evidence2.put("medicationsCount", "10");
    ar2.setEvidenceCountSummary(evidence2);
    ar2.setEvidenceCount(10);
    contention2.addAssessmentResult(ar2);
    contention2.addAssessmentResult(2);

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
            contention -> assertEquals(2, contention2.getAssessmentResults().size()),
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
