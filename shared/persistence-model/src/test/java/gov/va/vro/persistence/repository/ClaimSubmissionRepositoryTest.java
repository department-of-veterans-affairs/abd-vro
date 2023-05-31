package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@DataJpaTest
public class ClaimSubmissionRepositoryTest {
  @Autowired private ClaimSubmissionRepository claimSubmissionRepository;
  @Autowired private VeteranRepository veteranRepository;
  @Autowired private ClaimRepository claimRepository;

  @Test
  void test() {
    Date icnTimestamp = new Date();

    VeteranEntity veteran = TestDataSupplier.createVeteran("X", "Y", icnTimestamp);
    veteranRepository.save(veteran);
    EvidenceSummaryDocumentEntity evidenceSummaryDocument = new EvidenceSummaryDocumentEntity();
    Map<String, String> count1 = new HashMap<>();
    count1.put("count", "1");
    evidenceSummaryDocument.setEvidenceCount(count1);
    evidenceSummaryDocument.setDocumentName("documentName1");
    Map<String, String> evidence = new HashMap<>();
    evidence.put("medicationsCount", "10");
    AssessmentResultEntity assessmentResult = new AssessmentResultEntity();
    assessmentResult.setEvidenceCountSummary(evidence);
    ContentionEntity contention1 = new ContentionEntity("c1");
    contention1.addAssessmentResult(assessmentResult);
    contention1.addEvidenceSummaryDocument(evidenceSummaryDocument);
    ClaimEntity claim = TestDataSupplier.createClaim("123", veteran);
    claim.addContention(contention1);
    claim = claimRepository.save(claim);
    ClaimSubmissionEntity claimSubmission =
        TestDataSupplier.createClaimSubmission(claim, "collection1", "type");
    claimSubmission = claimSubmissionRepository.save(claimSubmission);
    assertNotNull(claimSubmission.getId());
    assertNotNull(claim.getCreatedAt());
  }
}
