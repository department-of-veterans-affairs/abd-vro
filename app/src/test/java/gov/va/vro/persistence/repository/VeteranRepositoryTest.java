package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VeteranRepositoryTest extends BaseIntegrationTest {

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
