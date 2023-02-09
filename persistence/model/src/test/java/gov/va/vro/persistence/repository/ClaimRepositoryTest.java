package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@DataJpaTest
public class ClaimRepositoryTest {
  @Autowired private VeteranRepository veteranRepository;
  @Autowired private ClaimRepository claimRepository;

  @Test
  void test() {
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
    var claim = TestDataSupplier.createClaim("123", veteran);
    claim.addContention(contention1);
    claim = claimRepository.save(claim);
    assertNotNull(claim.getId());
    assertNotNull(claim.getCreatedAt());
    assertNotNull(claim.getContentions().get(0));
    assertEquals(claim.getContentions().get(0).getDiagnosticCode(), "c1");
  }
}
