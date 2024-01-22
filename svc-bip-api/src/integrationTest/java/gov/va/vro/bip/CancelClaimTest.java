package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class CancelClaimTest extends BaseIntegrationTest {

  private static final String OPEN = "Open";
  private static final String CANCELLED = "Cancelled";

  @Test
  void testCancelClaim_200() {
    BipClaim existingClaim = getExistingClaim(CLAIM_ID_200);
    assertEquals(OPEN, existingClaim.getClaimLifecycleStatus());

    CancelClaimRequest request =
        CancelClaimRequest.builder()
            .claimId(CLAIM_ID_200)
            .closeReasonText("because we are testing")
            .lifecycleStatusReasonCode("60")
            .build();
    CancelClaimResponse response = sendAndReceive(cancelClaimQueue, request);
    assertBaseResponseIs2xx(response, HttpStatus.OK);

    BipClaim updatedClaim = getExistingClaim(CLAIM_ID_200);
    assertEquals(CANCELLED, updatedClaim.getClaimLifecycleStatus());
  }

  @Test
  void testPutTempStationOfJurisdiction_404() {
    CancelClaimRequest request =
        CancelClaimRequest.builder()
            .claimId(CLAIM_ID_404)
            .closeReasonText("because we are testing")
            .lifecycleStatusReasonCode("60")
            .build();
    CancelClaimResponse response = sendAndReceive(cancelClaimQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.NOT_FOUND);
  }

  @Test
  void testPutTempStationOfJurisdiction_500() {
    CancelClaimRequest request =
        CancelClaimRequest.builder()
            .claimId(CLAIM_ID_500)
            .closeReasonText("because we are testing")
            .lifecycleStatusReasonCode("60")
            .build();
    CancelClaimResponse response = sendAndReceive(cancelClaimQueue, request);
    assertBaseResponseIsNot2xx(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
