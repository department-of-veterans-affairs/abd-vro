package gov.va.vro.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.va.vro.bip.model.BipPayloadRequest;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.claim.BipClaim;
import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.ExistingContention;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.service.BipResetMockController;
import gov.va.vro.bip.service.IBipApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Slf4j
public class BaseIntegrationTest {
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private BipResetMockController resetMockController;
  @Autowired private IBipApiService bipApiService;

  protected static final long CLAIM_ID_200 = 1000L;
  protected static final long CLAIM_ID_404 = 0L;
  protected static final long CLAIM_ID_500 = 500;

  @Value("${exchangeName}")
  protected String exchangeName;

  @Value("${putTempStationOfJurisdictionQueue}")
  protected String putTempStationOfJurisdictionQueue;

  @Value("${getClaimContentionsQueue}")
  protected String getClaimContentionsQueue;

  @Value("${getClaimDetailsQueue}")
  protected String getClaimDetailsQueue;

  @Value("${cancelClaimQueue}")
  protected String cancelClaimQueue;

  @Value("${putClaimLifecycleStatusQueue}")
  protected String putClaimLifecycleStatusQueue;

  @Value("${updateClaimContentionsQueue}")
  protected String updateClaimContentionsQueue;

  @Value("${createClaimContentionsQueue}")
  protected String createClaimContentionsQueue;

  @Value("${getSpecialIssueTypesQueue}")
  protected String getSpecialIssueTypesQueue;

  @BeforeEach
  public void resetMock() {
    resetMockController.resetClaim(CLAIM_ID_200);
  }

  @SuppressWarnings("unchecked")
  protected <T extends BipPayloadResponse> T sendAndReceive(
      String queue, BipPayloadRequest request) {
    return (T) rabbitTemplate.convertSendAndReceive(exchangeName, queue, request);
  }

  protected ExistingContention getExistingContention() {
    GetClaimContentionsRequest request =
        GetClaimContentionsRequest.builder().claimId(CLAIM_ID_200).build();
    GetClaimContentionsResponse getResponse = bipApiService.getClaimContentions(request);
    assertBaseResponseIs2xx(getResponse, HttpStatus.OK);
    assertNotNull(getResponse.getContentions());
    assertEquals(1, getResponse.getContentions().size());
    return getResponse.getContentions().get(0);
  }

  protected BipClaim getExistingClaim() {
    GetClaimRequest request = GetClaimRequest.builder().claimId(CLAIM_ID_200).build();
    GetClaimResponse getResponse = bipApiService.getClaimDetails(request);
    assertBaseResponseIs2xx(getResponse, HttpStatus.OK);
    assertNotNull(getResponse.getClaim());
    return getResponse.getClaim();
  }

  protected void assertBaseResponseIs2xx(BipPayloadResponse response, HttpStatus status) {
    assertNotNull(response);
    assertEquals(status.value(), response.getStatusCode());
    assertEquals(status.name(), response.getStatusMessage());
    assertNull(response.getMessages());
  }

  protected void assertBaseResponseIsNot2xx(BipPayloadResponse response, HttpStatus status) {
    assertNotNull(response);
    assertEquals(status.value(), response.getStatusCode());
    assertEquals(status.name(), response.getStatusMessage());
    assertNotNull(response.getMessages());
    assertEquals(1, response.getMessages().size());
  }
}
