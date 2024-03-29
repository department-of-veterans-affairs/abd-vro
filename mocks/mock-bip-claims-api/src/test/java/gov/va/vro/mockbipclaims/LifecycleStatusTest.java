package gov.va.vro.mockbipclaims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import gov.va.vro.mockbipclaims.config.TestConfig;
import gov.va.vro.mockbipclaims.model.bip.ClaimDetail;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateClaimLifecycleStatusResponse;
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
public class LifecycleStatusTest {
  @LocalServerPort int port;

  @Autowired private TestHelper helper;

  @Test
  void positiveUpdateTest() {
    TestSpec spec = new TestSpec();
    spec.setClaimId(1010);
    spec.setPort(port);

    helper.resetUpdated(spec);
    boolean updatedBefore = helper.isLifecycleStatusUpdated(spec);
    assertEquals(false, updatedBefore);

    ClaimDetail claimDetail = helper.getClaimDetail(spec);
    String rfd = "Ready for Decision";
    assertNotEquals(rfd, claimDetail.getClaimLifecycleStatus());

    ResponseEntity<UpdateClaimLifecycleStatusResponse> response =
        helper.putLifecycleStatus(spec, rfd);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ClaimDetail claimDetailAfter = helper.getClaimDetail(spec);
    assertEquals(rfd, claimDetailAfter.getClaimLifecycleStatus());

    boolean updated = helper.isLifecycleStatusUpdated(spec);
    assertEquals(true, updated);

    helper.resetUpdated(spec);
    boolean updatedAfter = helper.isLifecycleStatusUpdated(spec);
    assertEquals(false, updatedAfter);
  }
}
