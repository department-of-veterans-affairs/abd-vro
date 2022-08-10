package gov.va.vro;

import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {

  @Autowired protected ClaimRepository claimRepository;

  @Autowired protected VeteranRepository veteranRepository;

  @BeforeEach
  @AfterEach
  public void delete() {
    claimRepository.deleteAll();
    veteranRepository.deleteAll();
  }
}
