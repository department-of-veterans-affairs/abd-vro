package gov.va.vro.bip;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipCloseClaimPayload;
import gov.va.vro.bip.model.BipCloseClaimReason;
import gov.va.vro.bip.model.BipCloseClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.HasStatusCodeAndMessage;
import gov.va.vro.bip.model.RequestForUpdateClaimStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Slf4j
class RabbitMqIntegrationTest {
  @Autowired RabbitTemplate rabbitTemplate;

  @Value("${exchangeName}")
  String exchangeName;

  // known good values from mocks/mock-bip-claims-api/src/main/resources/mock-claims.json
  private static final String CLAIM_ID1 = "1015";
  private static final long CANCEL_CLAIM_ID = 1370L;
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
  void testSetClaimToRfdStatus(@Value("${setClaimToRfdStatusQueue}") String qName) {
    BipUpdateClaimResp response =
        (BipUpdateClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, CLAIM_ID1);
    assertResponseIsSuccess(response);
  }

  @Test
  void testCancelClaim(@Value("${cancelClaimQueue}") String qName) {
    BipCloseClaimReason reason =
        BipCloseClaimReason.builder()
            .closeReasonText("because we are testing")
            .lifecycleStatusReasonCode("60")
            .build();
    BipCloseClaimPayload req =
        BipCloseClaimPayload.builder().claimId(CANCEL_CLAIM_ID).reason(reason).build();
    BipCloseClaimResp response =
        (BipCloseClaimResp) rabbitTemplate.convertSendAndReceive(exchangeName, qName, req);
    assertResponseIsSuccess(response);
  }

  @SneakyThrows
  private void assertResponseIsSuccess(BipCloseClaimResp response) {
    log.info("response: {}", response);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.statusCode, 200);
  }

  @SneakyThrows
  private void assertResponseIsSuccess(HasStatusCodeAndMessage response) {
    log.info("response: {}", response);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.statusCode, 200);
    // There should be a message with 'Success' in the 'text' field
    JsonNode node = mapper.readTree(response.statusMessage);
    List<String> textFields = node.findValuesAsText("text");
    Assertions.assertFalse(textFields.isEmpty());
    Assertions.assertTrue(textFields.contains("Success"));
  }
}
