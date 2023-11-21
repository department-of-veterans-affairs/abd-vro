package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.bip.model.Contention;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public class CreateClaimContentionsTest extends BaseIntegrationTest {

  @Test
  void testCreateClaimContentionsWithSingleContention_200() {
    CreateClaimContentionsRequest updateRequest =
        CreateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_200)
            .createContentions(List.of(getNewContention()))
            .build();
    CreateClaimContentionsResponse response =
        sendAndReceive(createClaimContentionsQueue, updateRequest);
    assertBaseResponseIs2xx(response, HttpStatus.CREATED);

    assertNotNull(response.getContentionIds());
    assertEquals(1, response.getContentionIds().size());
  }

  @Test
  void testCreateClaimContentionsWithMultipleContentions_200() {
    CreateClaimContentionsRequest updateRequest =
        CreateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_200)
            .createContentions(List.of(getNewContention(), getNewContention()))
            .build();
    CreateClaimContentionsResponse response =
        sendAndReceive(createClaimContentionsQueue, updateRequest);
    assertBaseResponseIs2xx(response, HttpStatus.CREATED);

    assertNotNull(response.getContentionIds());
    assertEquals(2, response.getContentionIds().size());
  }

  @Test
  void testCreateClaimContentions_claimNotFound_400() {
    CreateClaimContentionsRequest updateRequest =
        CreateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_404)
            .createContentions(List.of())
            .build();
    CreateClaimContentionsResponse updateResponse =
        sendAndReceive(createClaimContentionsQueue, updateRequest);
    assertBaseResponseIsNot2xx(updateResponse, HttpStatus.BAD_REQUEST);
  }

  @Test
  void testCreateClaimContentions_500() {
    CreateClaimContentionsRequest updateRequest =
        CreateClaimContentionsRequest.builder()
            .claimId(CLAIM_ID_500)
            .createContentions(List.of())
            .build();
    CreateClaimContentionsResponse updateResponse =
        sendAndReceive(createClaimContentionsQueue, updateRequest);
    assertBaseResponseIsNot2xx(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private static Contention getNewContention() {
    return Contention.builder()
        .medicalInd(true)
        .beginDate(Instant.parse("2023-02-01T00:00:00Z"))
        .contentionTypeCode("NEW")
        .classificationType(1250)
        .diagnosticTypeCode(6260)
        .claimantText("tinnitus")
        .build();
  }
}
