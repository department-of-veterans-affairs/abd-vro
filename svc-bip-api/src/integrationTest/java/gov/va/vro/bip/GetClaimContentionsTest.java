package gov.va.vro.bip;

import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetClaimContentionsTest extends BaseIntegrationTest {

  @Test
  void testGetClaimContentions_200() {
    GetClaimContentionsRequest request =
        GetClaimContentionsRequest.builder().claimId(CLAIM_ID_200).build();
    GetClaimContentionsResponse response = sendAndReceive(getClaimContentionsQueue, request);
    assertBaseResponseIs200(response);
  }

  @Test
  void testGetClaimContentions_404() {
    GetClaimContentionsRequest request =
        GetClaimContentionsRequest.builder().claimId(CLAIM_ID_404).build();
    GetClaimContentionsResponse response = sendAndReceive(getClaimContentionsQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetClaimContentions_500() {
    GetClaimContentionsRequest request =
        GetClaimContentionsRequest.builder().claimId(CLAIM_ID_500).build();
    GetClaimContentionsResponse response = sendAndReceive(getClaimContentionsQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
