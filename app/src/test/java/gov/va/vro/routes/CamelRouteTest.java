package gov.va.vro.routes;

import gov.va.starter.example.service.spi.db.model.Claim;
import gov.va.vro.service.provider.CamelEntrance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CamelRouteTest {

    @Autowired
    private CamelEntrance camelEntrance;

    @Test
    void testCamel() {
        var claim = new Claim();
        claim.setClaimSubmissionId("id1");
        claim.setDiagnosticCode("134");
        claim.setVeteranIcn("icn");
        camelEntrance.saveClaim(claim);
    }
}
