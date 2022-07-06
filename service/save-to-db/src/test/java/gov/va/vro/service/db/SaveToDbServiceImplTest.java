package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.starter.example.service.spi.db.model.Contention;
import gov.va.starter.example.service.spi.db.model.Veteran;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional // TODO: remove and clean up
class SaveToDbServiceImplTest {

  @Autowired private SaveToDbServiceImpl saveToDbService;

  @Autowired private VeteranRepository veteranRepository;

  @Autowired private ClaimRepository claimRepository;

  @Test
  void persistClaim() {
    Claim claim = new Claim();
    claim.setClaimId("claim1");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    claim.setVeteran(veteran);
    Contention contention = new Contention();
    contention.setDiagnosticCode("1234");
    // TODO add more entities
    claim.getContentions().add(contention);
    saveToDbService.persistClaim(claim);

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
  }
}
