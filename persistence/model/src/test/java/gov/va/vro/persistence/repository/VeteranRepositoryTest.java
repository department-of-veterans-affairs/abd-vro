package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.persistence.model.VeteranEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class VeteranRepositoryTest {

  @Autowired private VeteranRepository veteranRepository;

  @Test
  void test() {
    VeteranEntity veteran = new VeteranEntity();
    veteran.setIcn("X");
    veteran.setParticipantId("Y");
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    var created = veteran.getCreatedAt();
    var updated = veteran.getUpdatedAt();
    assertNotNull(created);
    assertNotNull(updated);
  }
}
