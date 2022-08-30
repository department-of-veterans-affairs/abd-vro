package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class VeteranRepositoryTest {

  @Autowired private VeteranRepository veteranRepository;

  @Test
  void test() {
    var veteran = TestDataSupplier.createVeteran("X", "Y");
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    var created = veteran.getCreatedAt();
    var updated = veteran.getUpdatedAt();
    assertNotNull(created);
    assertNotNull(updated);
  }
}
