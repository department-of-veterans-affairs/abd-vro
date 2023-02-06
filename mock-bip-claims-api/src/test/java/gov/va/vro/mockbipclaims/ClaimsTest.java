package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.mockbipclaims.configuration.TestConfig;
import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummary;
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

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ClaimsTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  @Test
  void happyPathTest() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    ResponseEntity<ClaimDetailResponse> response = helper.getClaim(spec);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClaimDetailResponse body = response.getBody();
    ClaimDetail claim = body.getClaim();
    String tempStationOfJurisdiction = claim.getTempStationOfJurisdiction();
    assertEquals("398", tempStationOfJurisdiction);

    ResponseEntity<ContentionSummariesResponse> response2 = helper.getContentions(spec);
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    ContentionSummariesResponse csr = response2.getBody();
    List<ContentionSummary> contentions = csr.getContentions();
    assertEquals(1, contentions.size());
    ContentionSummary summary = contentions.get(0);
    var codes = summary.getSpecialIssueCodes();
    assertTrue(codes.indexOf("rrd") >= 0);
    assertTrue(codes.indexOf("AOOV") >= 0);
  }
}
