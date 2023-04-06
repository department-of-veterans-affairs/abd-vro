package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.mockbipclaims.config.TestConfig;
import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.PhaseType;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.mock.response.SuccessResponse;
import gov.va.vro.mockbipclaims.util.TestHelper;
import gov.va.vro.mockbipclaims.util.TestSpec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ClaimsTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  private void verifyClaim(TestSpec spec, String expectedTsor) {
    ResponseEntity<ClaimDetailResponse> response = helper.getClaim(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClaimDetailResponse body = response.getBody();
    ClaimDetail claim = body.getClaim();
    String tempStationOfJurisdiction = claim.getTempStationOfJurisdiction();
    assertEquals(expectedTsor, tempStationOfJurisdiction);
    assertEquals(PhaseType.GATHERING_OF_EVIDENCE, claim.getPhase());
  }

  @Test
  void claim1010Test() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    verifyClaim(spec, "398");

    String expectedTsor = "456";
    ResponseEntity<SuccessResponse> response =
        helper.postClaimTempJurisdictionStation(spec, expectedTsor);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verifyClaim(spec, expectedTsor);
  }
}
