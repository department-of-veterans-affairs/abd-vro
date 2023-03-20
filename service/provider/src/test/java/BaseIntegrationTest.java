import gov.va.vro.persistence.repository.AuditEventRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

  @Autowired protected ClaimRepository claimRepository;

  @Autowired protected ClaimSubmissionRepository claimSubmissionRepository;

  @Autowired protected VeteranRepository veteranRepository;

  @Autowired protected AuditEventRepository auditEventRepository;

  /** Delete all from repositories. */
  @BeforeEach
  @AfterEach
  public void delete() {
    claimRepository.deleteAll();
    // Claim repository will cascade delete from claimSubmission
    veteranRepository.deleteAll();
    auditEventRepository.deleteAll();
  }
}
