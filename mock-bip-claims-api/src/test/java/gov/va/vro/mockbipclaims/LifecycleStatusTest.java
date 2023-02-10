package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.mockbipclaims.configuration.TestConfig;
import gov.va.vro.mockbipclaims.model.ClaimDetail;
import gov.va.vro.mockbipclaims.model.ClaimLifecycleStatusesResponse;
import gov.va.vro.mockbipclaims.util.TestHelper;
import gov.va.vro.mockbipclaims.util.TestSpec;
import gov.va.vro.model.bip.ClaimStatus;
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
public class LifecycleStatusTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  @Test
  void positiveUpdateTest() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    final String[] actionsBefore = helper.getModifyingActions(spec);

    ClaimDetail claimDetail = helper.getClaimDetail(spec);
    String rfd = ClaimStatus.RFD.getDescription();
    assertNotEquals(rfd, claimDetail.getClaimLifecycleStatus());

    ResponseEntity<ClaimLifecycleStatusesResponse> response = helper.putLifecycleStatus(spec, rfd);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ClaimDetail claimDetailAfter = helper.getClaimDetail(spec);
    assertEquals(rfd, claimDetailAfter.getClaimLifecycleStatus());

    String[] actionsAfter = helper.getModifyingActions(spec);
    int actionsBeforeLength = actionsBefore.length;
    assertTrue(actionsAfter.length > actionsBeforeLength);
  }
}
