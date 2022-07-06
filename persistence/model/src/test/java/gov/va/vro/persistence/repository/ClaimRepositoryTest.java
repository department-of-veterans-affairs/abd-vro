package gov.va.vro.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.persistence.model.ContentionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class ClaimRepositoryTest {

  @Autowired private ClaimRepository claimRepository;

  @Autowired private VeteranRepository veteranRepository;

  @Test
  void test() {
    var veteran = TestDataSupplier.createVeteran("X", "Y");
    veteranRepository.save(veteran);
    assertNotNull(veteran.getIcn());
    assertNotNull(veteran.getCreatedAt());
    assertNotNull(veteran.getUpdatedAt());

    var claim = TestDataSupplier.createClaim("123", "type", veteran);

    ContentionEntity contention1 = new ContentionEntity("c1");
    contention1.addAssessmentResult(2);
    contention1.addEvidenceSummaryDocument("doc1", 1);
    contention1.addEvidenceSummaryDocument("doc2", 2);
    ContentionEntity contention2 = new ContentionEntity("c2");
    contention2.addAssessmentResult(1);
    contention2.addAssessmentResult(2);
    claim.addContention(contention1);
    claim.addContention(contention2);

    claim = claimRepository.save(claim);
    assertNotNull(claim.getId());
    assertNotNull(claim.getCreatedAt());
    List<ContentionEntity> contentions = claim.getContentions();
    assertEquals(2, contentions.size());
    contentions.forEach(contention -> assertNotNull(contention.getId()));
    contentions.stream()
        .filter(contention -> "c1".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention -> assertEquals(2, contention1.getEvidenceSummaryDocuments().size()),
            Assertions::fail);
    contentions.stream()
        .filter(contention -> "c2".equals(contention.getDiagnosticCode()))
        .findAny()
        .ifPresentOrElse(
            contention -> assertEquals(2, contention2.getAssessmentResults().size()),
            Assertions::fail);
  }
}
