package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ClaimRepositoryTest {

  @Autowired private ClaimRepository claimRepository;

  @Autowired private VeteranRepository veteranRepository;

  @Test
  void test() {
    VeteranEntity veteran = new VeteranEntity();
    veteran.setIcn("X");
    veteran.setParticipantId("Y");
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    assertNotNull(veteran.getCreatedAt());
    assertNotNull(veteran.getUpdatedAt());

    ClaimEntity claim = new ClaimEntity();
    claim.setClaimId("123");
    claim.setIdType("type");
    claim.setVeteran(veteran);

    ContentionEntity contention1 = new ContentionEntity("c1");
    ContentionEntity contention2 = new ContentionEntity("c2");
    claim.addContention(contention1);
    claim.addContention(contention2);

    claim = claimRepository.save(claim);
    assertNotNull(claim.getId());
    assertNotNull(claim.getCreatedAt());
    assertEquals(2, claim.getContentions().size());
    claim.getContentions().forEach(contention -> assertNotNull(contention.getId()));
  }
}
