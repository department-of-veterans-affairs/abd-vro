package gov.va.vro.routes;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.service.provider.CamelEntrance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClaimRouteTest {

  @Autowired private CamelEntrance camelEntrance;

  @Test
  void camelEntrance() {
    var claimSubmission =
        ClaimSubmission.builder()
            .submissionId("id")
            .claimantId("id")
            .pii("pii")
            .contentionType("123")
            .firstName("first")
            .lastName("last")
            .userName("user")
            .status(ClaimSubmission.ClaimStatus.CREATED)
            .build();
    camelEntrance.postClaim(claimSubmission);
  }
}
