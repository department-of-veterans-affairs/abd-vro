package gov.va.vro.bip;

import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleRequest;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PutClaimLifecycleStatusTest extends BaseIntegrationTest {

  @Test
  void testPutClaimLifecycleStatusTest_200() {
    PutClaimLifecycleRequest request =
        PutClaimLifecycleRequest.builder()
            .claimId(CLAIM_ID_200)
            .claimLifecycleStatus("Ready for Decision")
            .build();
    PutClaimLifecycleResponse response = sendAndReceive(putClaimLifecycleStatusQueue, request);
    assertBaseResponseIs2xx(response, HttpStatus.OK);
  }

  @Test
  void testPutClaimLifecycleStatusTest_404() {
    PutClaimLifecycleRequest request =
        PutClaimLifecycleRequest.builder()
            .claimId(CLAIM_ID_404)
            .claimLifecycleStatus("Ready for Decision")
            .build();
    PutClaimLifecycleResponse response = sendAndReceive(putClaimLifecycleStatusQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.NOT_FOUND);
  }

  @Test
  void testPutClaimLifecycleStatusTest_500() {
    PutClaimLifecycleRequest request =
        PutClaimLifecycleRequest.builder()
            .claimId(CLAIM_ID_500)
            .claimLifecycleStatus("Ready for Decision")
            .build();
    PutClaimLifecycleResponse response = sendAndReceive(putClaimLifecycleStatusQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
