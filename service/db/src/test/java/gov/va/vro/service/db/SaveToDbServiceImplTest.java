package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.spi.db.model.Claim;
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
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode("1234");
    var result = saveToDbService.insertClaim(claim);
    assertNotNull(result.getRecordId());
    assertEquals(claim.getClaimId(), result.getClaimId());
    assertEquals(claim.getIdType(), result.getIdType());
    assertEquals(claim.getDiagnosticCode(), result.getDiagnosticCode());
    assertEquals(claim.getVeteranIcn(), result.getVeteranIcn());
    assertEquals(claim.getIncomingStatus(), result.getIncomingStatus());

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
    ClaimEntity claimEntity =
        claimRepository.findByClaimIdAndIdType(claim.getClaimId(), claim.getIdType()).orElseThrow();
    assertEquals(claim.getClaimId(), claimEntity.getClaimId());
    assertEquals("va.gov-Form526Submission", claimEntity.getIdType());
    assertEquals("submission", claimEntity.getIncomingStatus());
    assertEquals(claim.getVeteranIcn(), claimEntity.getVeteran().getIcn());
    assertEquals(1, claimEntity.getContentions().size());
    ContentionEntity contentionEntity = claimEntity.getContentions().get(0);
    assertEquals(claim.getDiagnosticCode(), contentionEntity.getDiagnosticCode());
  }
}
