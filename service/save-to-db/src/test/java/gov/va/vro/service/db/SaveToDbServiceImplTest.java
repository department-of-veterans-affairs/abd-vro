package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.starter.example.service.spi.db.model.*;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class SaveToDbServiceImplTest {

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private VeteranRepository veteranRepository;

  @Autowired private ClaimRepository claimRepository;

  @Test
  void persistClaim() {
    Claim claim = new Claim();
    claim.setClaimId("claim1");
    claim.setIdType("type");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    claim.setVeteran(veteran);
    Contention contention = new Contention();
    contention.setDiagnosticCode("1234");
    AssessmentResult assessmentResult = new AssessmentResult();
    assessmentResult.setEvidenceCount(2);
    contention.getAssessmentResults().add(assessmentResult);
    EvidenceSummaryDocument evidenceSummaryDocument = new EvidenceSummaryDocument();
    evidenceSummaryDocument.setDocumentName("doc");
    evidenceSummaryDocument.setEvidenceCount(4);
    contention.getEvidenceSummaryDocuments().add(evidenceSummaryDocument);
    claim.getContentions().add(contention);
    saveToDbService.persistClaim(claim);

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
    ClaimEntity claimEntity =
        claimRepository.findByClaimIdAndIdType(claim.getClaimId(), claim.getIdType()).orElseThrow();
    assertEquals(1, claimEntity.getContentions().size());
    ContentionEntity contentionEntity = claimEntity.getContentions().get(0);
    assertEquals(contention.getDiagnosticCode(), contentionEntity.getDiagnosticCode());
    assertEquals(1, contentionEntity.getEvidenceSummaryDocuments().size());
    assertEquals(1, contentionEntity.getAssessmentResults().size());
  }
}
