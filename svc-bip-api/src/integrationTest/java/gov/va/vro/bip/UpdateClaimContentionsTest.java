package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.va.vro.bip.model.contentions.ExistingContention;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UpdateClaimContentionsTest extends BaseIntegrationTest {

  @Test
  void testUpdateClaimContentions_200() {
    ExistingContention original = getExistingContention();
    assertNull(original.getAltContentionName());
    String altContentionName = "This didnt exist";
    ExistingContention modified = original.toBuilder().altContentionName(altContentionName).build();

    UpdateClaimContentionsRequest updateRequest =
        UpdateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_200)
            .updateContentions(List.of(modified))
            .build();
    UpdateClaimContentionsResponse updateResponse =
        sendAndReceive(updateClaimContentionsQueue, updateRequest);
    assertBaseResponseIs2xx(updateResponse, HttpStatus.OK);

    ExistingContention updated = getExistingContention();
    assertEquals(altContentionName, updated.getAltContentionName());
  }

  @Test
  void testUpdateClaimContentions_claimNotFound_400() {
    UpdateClaimContentionsRequest updateRequest =
        UpdateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_404)
            .updateContentions(List.of())
            .build();
    UpdateClaimContentionsResponse updateResponse =
        sendAndReceive(updateClaimContentionsQueue, updateRequest);
    assertBaseResponseIsNot2xx(updateResponse, HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUpdateClaimContentions_contentionIdNotFound_400() {
    UpdateClaimContentionsRequest updateRequest =
        UpdateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_200)
            .updateContentions(List.of(ExistingContention.builder().contentionId(0L).build()))
            .build();
    UpdateClaimContentionsResponse updateResponse =
        sendAndReceive(updateClaimContentionsQueue, updateRequest);
    assertBaseResponseIsNot2xx(updateResponse, HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUpdateClaimContentions_500() {
    UpdateClaimContentionsRequest updateRequest =
        UpdateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_500)
            .updateContentions(List.of())
            .build();
    UpdateClaimContentionsResponse updateResponse =
        sendAndReceive(updateClaimContentionsQueue, updateRequest);
    assertBaseResponseIsNot2xx(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
