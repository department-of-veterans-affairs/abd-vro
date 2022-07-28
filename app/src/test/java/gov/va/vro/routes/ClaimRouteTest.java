package gov.va.vro.routes;

import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.db.model.Claim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClaimRouteTest {

  @Autowired private CamelEntrance camelEntrance;

  @Test
  void camelEntrance() {
    var claim =
        Claim.builder().claimSubmissionId("id").diagnosticCode("1234").veteranIcn("icn").build();
    camelEntrance.processClaim(claim);
  }
}
