package gov.va.vro.bip;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import gov.va.vro.bip.model.UpdateContention;
import gov.va.vro.bip.model.UpdateContentionModel;
import gov.va.vro.bip.model.UpdateContentionReq;
import gov.va.vro.bip.service.BipApiService;
import gov.va.vro.bip.service.RabbitMqController;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class RabbitMqIntegrationTest {
  @Autowired BipApiService service;
  @Autowired RabbitMqController controller;
  @Autowired RabbitTemplate rabbitTemplate;
  @Autowired RabbitAdmin rabbitAdmin;

  @Value("${exchangeName}")
  String exchangeName;

  // known good values from mocks/mock-bip-claims-api/src/main/resources/mock-claims.json
  private static final String CLAIM_ID1 = "1015";
  private static final long CLAIM_ID1_LONG = 1015L;
  private static final long CONTENTION_ID = 1011L;
  final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testUpdateClaimStatus(@Value("${updateClaimStatusQueue}") String qName) {
    RequestForUpdateClaimStatus request =
        new RequestForUpdateClaimStatus(ClaimStatus.RFD, Long.parseLong(CLAIM_ID1));
    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, request);
    assertResponseIsSuccess(response);
  }

  @Test
  void testGetClaimDetails(@Value("${getClaimDetailsQueue}") String qName) {
    BipClaimResp response =
        (BipClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, CLAIM_ID1);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(200, response.statusCode);
    Assertions.assertEquals(CLAIM_ID1, response.getClaim().getClaimId());
    Assertions.assertEquals("Gathering of Evidence", response.getClaim().getPhase());
    Assertions.assertEquals("Ready for Decision", response.getClaim().getClaimLifecycleStatus());
    Assertions.assertNotNull(response.getClaim().getTempStationOfJurisdiction());
  }

  @Test
  void testSetClaimToRfdStatus(@Value("${setClaimToRfdStatusQueue}") String qName) {
    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, CLAIM_ID1);
    assertResponseIsSuccess(response);
  }

  @SneakyThrows
  @Test
  void testUpdateClaimContention(@Value("${updateClaimContentionQueue}") String qName) {
    UpdateContention builtContention =
        UpdateContention.builder().contentionId(CONTENTION_ID).build();
    List<UpdateContention> builtUpdates = Arrays.asList(builtContention);
    UpdateContentionReq testReq =
        UpdateContentionReq.builder().updateContentions(builtUpdates).build();
    UpdateContentionModel req =
        UpdateContentionModel.builder().claimId(CLAIM_ID1_LONG).updateContentions(testReq).build();

    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);
    assertResponseIsSuccess(response);
  }

  @SneakyThrows
  private void assertResponseIsSuccess(BipUpdateClaimResp response) {
    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.statusCode, 200);
    // There should be a message with 'Success' in the 'text' field
    JsonNode node = mapper.readTree(response.statusMessage);
    List<String> textFields = node.findValuesAsText("text");
    Assertions.assertFalse(textFields.isEmpty());
    Assertions.assertTrue(textFields.contains("Success"));
  }

  private void assertResponseIsSuccess(BipPayloadResponse response) {
    Assertions.assertNotNull(response);
    Assertions.assertEquals(200, response.getStatusCode());
    Assertions.assertEquals("OK", response.getStatusMessage());
    Assertions.assertNull(response.getMessages());
  }
}
