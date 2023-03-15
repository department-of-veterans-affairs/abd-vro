package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.BipApiProps;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipApiService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * BIP service tests.
 *
 * @author warren @Date 2/2/23
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
public class BipApiServiceTest {
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
  private static final String CONTENTION = "/claims/%s/contentions";
  private static final String HTTPS = "https://";
  private static final String CLAIM_URL = "claims.bip.va.gov";
  private static final String CLAIM_SECRET = "secret";
  private static final String CLAIM_USERID = "userid";
  private static final String CLAIM_ISSUER = "issuer";
  private static final String STATION_ID = "280";
  private static final String APP_ID = "bip";
  private static final String APP_NAME = "vro";
  private static final String CLAIM_JTI = "032-83583455";

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
    props.setJti(CLAIM_JTI);
    props.setApplicationName(APP_NAME);
    props.setStationId(STATION_ID);
    props.setClaimClientId(CLAIM_USERID);

    Claims claims = props.toCommonJwtClaims();

    Mockito.doReturn(CLAIM_URL).when(bipApiProps).getClaimBaseUrl();
    Mockito.doReturn(CLAIM_ISSUER).when(bipApiProps).getClaimIssuer();
    Mockito.doReturn(CLAIM_SECRET).when(bipApiProps).getClaimSecret();
    Mockito.doReturn(claims).when(bipApiProps).toCommonJwtClaims();
  }

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
    try {
      BipClaim result = service.getClaimDetails(GOOD_CLAIM_ID);
      assertNotNull(result);
    } catch (BipException e) {
      log.error("Positive getClaimDetails test failed.", e);
      fail();
    }

    try {
      BipClaim result = service.getClaimDetails(BAD_CLAIM_ID);
      log.error("Negative getClaimDetails test failed. {}", result.getClaimId());
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.NOT_FOUND, e.getStatus());
    }
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
    try {
      BipUpdateClaimResp result = service.setClaimToRfdStatus(GOOD_CLAIM_ID);
      assertSame(result.getStatus(), HttpStatus.OK);
    } catch (BipException e) {
      log.error("Positive setClaimToRfdStatus test failed.", e);
      fail();
    }

    try {
      BipUpdateClaimResp result = service.setClaimToRfdStatus(BAD_CLAIM_ID);
      log.error("Negative setClaimToRfdStatus test failed.");
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
    }
  }

  @Test
  public void testGetClaimContention() throws Exception {
    String resp200Body = getTestData(CONTENTION_RESPONSE_200);
    String resp404Body = getTestData(CLAIM_RESPONSE_404);
    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);
    ResponseEntity<String> resp404 = ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp404Body);

    String baseUrl = HTTPS + CLAIM_URL;
    String goodUrl = baseUrl + String.format(CONTENTION, GOOD_CLAIM_ID);
    String badUrl = baseUrl + String.format(CONTENTION, BAD_CLAIM_ID);

    Mockito.doReturn(resp200)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(goodUrl),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
    Mockito.doReturn(resp404)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(badUrl),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));

    mockBipApiProp();
    try {
      List<ClaimContention> result = service.getClaimContentions(GOOD_CLAIM_ID);
      assertTrue(result.size() > 0);
    } catch (BipException e) {
      log.error("Positive getClaimContentions test failed.", e);
      fail();
    }

    try {
      List<ClaimContention> result = service.getClaimContentions(BAD_CLAIM_ID);
      log.error("Negative getClaimContentions test failed.");
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.NOT_FOUND, e.getStatus());
    }
  }

  @Test
  public void testUpdateClaimContention() throws Exception {
    String resp200Body = getTestData(CONTENTION_RESPONSE_200);
    String resp412Body = getTestData(CONTENTION_RESPONSE_412);
    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);
    ResponseEntity<String> resp412 =
        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(resp412Body);

    String baseUrl = HTTPS + CLAIM_URL;
    String goodUrl = baseUrl + String.format(CONTENTION, GOOD_CLAIM_ID);
    String badUrl = baseUrl + String.format(CONTENTION, BAD_CLAIM_ID);

    Mockito.doReturn(resp200)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(goodUrl),
            ArgumentMatchers.eq(HttpMethod.PUT),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
    Mockito.doReturn(resp412)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(badUrl),
            ArgumentMatchers.eq(HttpMethod.PUT),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));

    mockBipApiProp();
    UpdateContentionReq request = UpdateContentionReq.builder().build();
    try {
      BipUpdateClaimResp result = service.updateClaimContention(GOOD_CLAIM_ID, request);
      assertSame(HttpStatus.OK, result.getStatus());
    } catch (BipException e) {
      log.error("Positive updateClaimContention test failed.", e);
      fail();
    }

    try {
      BipUpdateClaimResp result = service.updateClaimContention(BAD_CLAIM_ID, request);
      log.error("Negative updateClaimContention test failed.");
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.PRECONDITION_FAILED, e.getStatus());
    }
  }
}
