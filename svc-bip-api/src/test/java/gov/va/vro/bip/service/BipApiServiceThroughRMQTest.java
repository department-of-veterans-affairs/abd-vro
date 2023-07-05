package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class BipApiServiceThroughRMQTest {

  @Test
  public void testGetClaimDetails() throws Exception {

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
    mockBipApiProp();

    BipClaim result =
        (BipClaim)
            rabbitTemplate.convertSendAndReceive(
                exchangeName, "getClaimDetailsQueue", GOOD_CLAIM_ID);
    assertEquals(500, result.statusCode);

    result =
        (BipClaim)
            rabbitTemplate.convertSendAndReceive(
                exchangeName, "getClaimDetailsQueue", BAD_CLAIM_ID);
    assertEquals(500, result.statusCode);
  }

  @Test
  public void testSetClaimToRfdStatus() {
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
    mockBipApiProp();

    // positive test
    BipUpdateClaimResp result =
        (BipUpdateClaimResp)
            rabbitTemplate.convertSendAndReceive(
                exchangeName, "setClaimToRfdStatusQueue", GOOD_CLAIM_ID);
    assertEquals(500, result.statusCode);

    // negative test
    result =
        (BipUpdateClaimResp)
            rabbitTemplate.convertSendAndReceive(
                exchangeName, "setClaimToRfdStatusQueue", BAD_CLAIM_ID);
    assertEquals(500, result.statusCode);
  }

  @BeforeEach
  private void setUp() {
    rabbitAdmin.purgeQueue(queueName, true);
  }

  @AfterEach
  private void tearDown() {
    rabbitAdmin.purgeQueue(queueName, true);
  }

  static String queueName = null;
  static String exchangeName = "bipApiExchange";
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private RabbitAdmin rabbitAdmin;

  private static final long GOOD_CLAIM_ID = 9666959L;

  private static final long BAD_CLAIM_ID = 9666958L;
  private static final String CONTENTION_RESPONSE_200 =
      "bip-test-data/contention_response_200.json";
  private static final String CONTENTION_RESPONSE_412 =
      "bip-test-data/contention_response_412.json";

  private static final String CLAIM_RESPONSE_404 = "bip-test-data/claim_response_404.json";
  private static final String CLAIM_RESPONSE_200 = "bip-test-data/claim_response_200.json";
  private static final String CLAIM_DETAILS = "/claims/%s";
  private static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  private static final String HTTPS = "https://";
  private static final String CLAIM_URL = "claims.bip.va.gov";
  private static final String CLAIM_SECRET = "secret";
  private static final String CLAIM_USERID = "userid";
  private static final String CLAIM_ISSUER = "issuer";
  private static final String STATION_ID = "280";
  private static final String APP_ID = "bip";
  @InjectMocks private BipApiService service;
  @Mock private RestTemplate restTemplate;
  @Mock private BipApiProps bipApiProps;

  private String getTestData(String dataFile) throws Exception {
    String filename =
        Objects.requireNonNull(getClass().getClassLoader().getResource(dataFile)).getPath();
    Path filePath = Path.of(filename);
    return Files.readString(filePath);
  }

  private void mockBipApiProp() {
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
