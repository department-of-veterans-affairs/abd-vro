package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Does same thing as BipApiServiceTest but through RMQ instance. Assumes RMQ broker is available
 * locally.
 */
@Disabled("needs an RMQ broker, which is not available in github build env.")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class RMQIntegrationTest {
  @Autowired BipApiService service;
  @MockBean RestTemplate restTemplate;
  @MockBean BipApiProps bipApiProps;
  @Autowired RabbitTemplate rabbitTemplate;
  @Autowired RabbitAdmin rabbitAdmin;

  @Test
  void testPositiveSetClaimToRfdStatus() {
    final String qName = setClaimToRfdStatusQueue;
    rabbitAdmin.purgeQueue(qName, true);
    mockRestTemplateForTestSetClaimToRfdStatus();
    mockBipApiProp();

    BipUpdateClaimResp result =
        (BipUpdateClaimResp)
            rabbitTemplate.convertSendAndReceive(exchangeName, qName, GOOD_CLAIM_ID);

    assertEquals(200, result.statusCode);
  }

  @Test
  void testNegativeSetClaimToRfdStatus() {
    final String qName = setClaimToRfdStatusQueue;
    rabbitAdmin.purgeQueue(qName, true);
    mockRestTemplateForTestSetClaimToRfdStatus();
    mockBipApiProp();

    BipUpdateClaimResp result =
        (BipUpdateClaimResp)
            rabbitTemplate.convertSendAndReceive(exchangeName, qName, BAD_CLAIM_ID);

    assertEquals(500, result.statusCode);
  }

  @Test
  void testPositiveGetClaimDetails() throws Exception {
    final String qName = getClaimDetailsQueue;
    mockRestTemplateForTestGetClaimDetails();
    rabbitAdmin.purgeQueue(qName, true);
    mockBipApiProp();

    BipClaim result =
        (BipClaim) rabbitTemplate.convertSendAndReceive(exchangeName, qName, GOOD_CLAIM_ID);

    assertEquals(200, result.statusCode);
  }

  @Test
  void testNegativeGetClaimDetails() throws Exception {
    final String qName = getClaimDetailsQueue;
    mockRestTemplateForTestGetClaimDetails();
    rabbitAdmin.purgeQueue(qName, true);
    mockBipApiProp();

    BipClaim result =
        (BipClaim) rabbitTemplate.convertSendAndReceive(exchangeName, qName, BAD_CLAIM_ID);

    assertEquals(500, result.statusCode);
  }

  void mockRestTemplateForTestGetClaimDetails() throws Exception {
    String resp200Body = getTestData(CLAIM_RESPONSE_200);
    String resp404Body = getTestData(CLAIM_RESPONSE_404);

    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);
    ResponseEntity<String> resp404 = ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp404Body);
    String baseUrl = HTTPS + CLAIM_URL;
    String claimUrl = baseUrl + String.format(CLAIM_DETAILS, GOOD_CLAIM_ID);
    String badClaimUrl = baseUrl + String.format(CLAIM_DETAILS, BAD_CLAIM_ID);

    Mockito.doReturn(resp200)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(claimUrl),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
    Mockito.doReturn(resp404)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(badClaimUrl),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
  }

  void mockRestTemplateForTestSetClaimToRfdStatus() {
    String baseUrl = HTTPS + CLAIM_URL;
    String goodUrl = baseUrl + String.format(UPDATE_CLAIM_STATUS, GOOD_CLAIM_ID);
    String badUrl = baseUrl + String.format(UPDATE_CLAIM_STATUS, BAD_CLAIM_ID);
    ResponseEntity<String> resp200 = ResponseEntity.ok("{}");
    ResponseEntity<String> resp500 =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
    Mockito.doReturn(resp200)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(goodUrl),
            ArgumentMatchers.eq(HttpMethod.PUT),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
    Mockito.doReturn(resp500)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(badUrl),
            ArgumentMatchers.eq(HttpMethod.PUT),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
  }

  @Value("${setClaimToRfdStatusQueue}")
  String setClaimToRfdStatusQueue;

  @Value("${getClaimDetailsQueue}")
  String getClaimDetailsQueue;

  @Value("${exchangeName}")
  String exchangeName;

  static final long GOOD_CLAIM_ID = 9666959L;
  static final long BAD_CLAIM_ID = 9666958L;
  static final String CLAIM_RESPONSE_404 = "bip-test-data/claim_response_404.json";
  static final String CLAIM_RESPONSE_200 = "bip-test-data/claim_response_200.json";
  static final String CLAIM_DETAILS = "/claims/%s";
  static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  static final String HTTPS = "https://";
  static final String CLAIM_URL = "claims.bip.va.gov";
  static final String CLAIM_SECRET = "secret";
  static final String CLAIM_USERID = "userid";
  static final String CLAIM_ISSUER = "issuer";
  static final String STATION_ID = "280";
  static final String APP_ID = "bip";

  String getTestData(String dataFile) throws Exception {
    String filename =
        Objects.requireNonNull(getClass().getClassLoader().getResource(dataFile)).getPath();
    Path filePath = Path.of(filename);
    return Files.readString(filePath);
  }

  void mockBipApiProp() {
    BipApiProps props = new BipApiProps();
    props.setApplicationId(APP_ID);
    props.setApplicationId(CLAIM_ISSUER);
    props.setStationId(STATION_ID);
    props.setClaimClientId(CLAIM_USERID);

    Claims claims = props.toCommonJwtClaims();

    Mockito.doReturn(CLAIM_URL).when(bipApiProps).getClaimBaseUrl();
    Mockito.doReturn(CLAIM_ISSUER).when(bipApiProps).getClaimIssuer();
    Mockito.doReturn(CLAIM_SECRET).when(bipApiProps).getClaimSecret();
    Mockito.doReturn(claims).when(bipApiProps).toCommonJwtClaims();
  }
}
