package gov.va.vro.service.db;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import gov.va.vro.service.db.model.ClaimRequest;
import gov.va.vro.service.db.model.Contention;
import gov.va.vro.service.db.model.Veteran;
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
    ClaimRequest claimRequest = new ClaimRequest();
    claimRequest.setClaimId("claim1");
    Veteran veteran = new Veteran();
    veteran.setIcn("v1");
    claimRequest.setVeteran(veteran);
    Contention contention = new Contention();
    contention.setDiagnosticCode("1234");
    claimRequest.getContentions().add(contention);
    saveToDbService.persistClaim(claimRequest);

    assertEquals(1, veteranRepository.findAll().size());
    assertEquals(1, claimRepository.findAll().size());
  }
}
