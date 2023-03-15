package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

@DataJpaTest
class VeteranRepositoryTest {

  @Autowired private VeteranRepository veteranRepository;

  @Test
  void test() {
    Date icnTimestamp = new Date();
    var veteran = TestDataSupplier.createVeteran("X", "Y", icnTimestamp);
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    assertNotNull(veteran.getIcnTimestamp());
    var created = veteran.getCreatedAt();
    var updated = veteran.getUpdatedAt();
    assertNotNull(created);
    assertNotNull(updated);
  }
}
