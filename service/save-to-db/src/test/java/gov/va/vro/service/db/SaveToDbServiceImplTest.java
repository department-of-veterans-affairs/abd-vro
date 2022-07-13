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
    claim.setClaimSubmissionId("claim1");
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("1234");
    saveToDbService.insertClaim(claim);

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
    ClaimEntity claimEntity =
        claimRepository
            .findByClaimIdAndIdType(claim.getClaimSubmissionId(), claim.getIdType())
            .orElseThrow();
    assertEquals(claim.getClaimSubmissionId(), claimEntity.getClaimId());
    assertEquals("va.gov-Form526Submission", claimEntity.getIdType());
    assertEquals("submission", claimEntity.getIncomingStatus());
    assertEquals(claim.getVeteranIcn(), claimEntity.getVeteran().getIcn());
    assertEquals(1, claimEntity.getContentions().size());
    ContentionEntity contentionEntity = claimEntity.getContentions().get(0);
    assertEquals(claim.getDiagnosticCode(), contentionEntity.getDiagnosticCode());
  }
}
