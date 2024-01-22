package gov.va.vro.bip;

import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetClaimDetailsTest extends BaseIntegrationTest {

  @Test
  void testGetClaim_200() {
    GetClaimRequest request = GetClaimRequest.builder().claimId(CLAIM_ID_200).build();
    GetClaimResponse response = sendAndReceive(getClaimDetailsQueue, request);
    assertBaseResponseIs2xx(response, HttpStatus.OK);
  }

  @Test
  void testGetClaim_404() {
    GetClaimRequest request = GetClaimRequest.builder().claimId(CLAIM_ID_404).build();
    GetClaimResponse response = sendAndReceive(getClaimDetailsQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetClaim_500() {
    GetClaimRequest request = GetClaimRequest.builder().claimId(CLAIM_ID_500).build();
    GetClaimResponse response = sendAndReceive(getClaimDetailsQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
