package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipCloseClaimPayload;
import gov.va.vro.bip.model.BipCloseClaimReason;
import gov.va.vro.bip.model.BipCloseClaimResp;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ExistingContention;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Stubber;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BipApiServiceTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final long GOOD_CLAIM_ID = 9666959L;
  private static final long BAD_CLAIM_ID = 9666958L;
  private static final long NOT_FOUND_CLAIM_ID = 9234567L;
  private static final long INTERNAL_SERVER_ERROR_CLAIM_ID = 9345678L;
  private static final String RESPONSE_500 = "bip-test-data/response_500.json";
  private static final String CONTENTION_RESPONSE_200 =
      "bip-test-data/contention_response_200.json";
  private static final String CLAIM_RESPONSE_404 = "bip-test-data/claim_response_404.json";
  private static final String CLAIM_RESPONSE_200 = "bip-test-data/claim_response_200.json";
  private static final String CLAIM_DETAILS = "/claims/%s";
  private static final String UPDATE_CLAIM_STATUS = "/claims/%s/lifecycle_status";
  private static final String CANCEL_CLAIM = "/claims/%s/cancel";
  private static final String CONTENTION = "/claims/%s/contentions";
  private static final String TEMP_STATION_OF_JURISDICTION =
      "/claims/%s/temporary_station_of_jurisdiction";
  private static final String HTTPS = "https://";
  private static final String CLAIM_URL = "claims.bip.va.gov";
  private static final String CLAIM_SECRET = "thesecretissecurenowthatitislongenough2184vnrwma";
  private static final String CLAIM_USERID = "userid";
  private static final String CLAIM_ISSUER = "issuer";
  private static final String STATION_ID = "280";
  private static final String APP_ID = "bip";
  // TODO get sample response
  private static final String API_RESPONSE_200 = "{\"mock response\"}";
  private static final String SPECIAL_ISSUE_TYPES = "/contentions/special_issue_types";

  @InjectMocks private BipApiService service;

  @Mock private RestTemplate restTemplate;

  @Mock private BipApiProps bipApiProps;

  private static String getTestData(String dataFile) throws Exception {
    String filename =
        Objects.requireNonNull(BipApiServiceTest.class.getClassLoader().getResource(dataFile))
            .getPath();
    Path filePath = Path.of(filename);
    return Files.readString(filePath);
  }

  @BeforeEach
  public void mockBipApiProp() {
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

  private String formatClaimUrl(String format, Long claimId) {
    String baseUrl = HTTPS + CLAIM_URL;
    return baseUrl + String.format(format, claimId);
  }

  private void mockResponseForUrl(Stubber response, String claimUrl, HttpMethod httpMethod) {
    response
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(claimUrl),
            ArgumentMatchers.eq(httpMethod),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
  }

  @Test
  public void testGetClaimDetails_200() throws Exception {
    String resp200Body = getTestData(CLAIM_RESPONSE_200);

    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);
    mockResponseForUrl(
        Mockito.doReturn(resp200), formatClaimUrl(CLAIM_DETAILS, GOOD_CLAIM_ID), HttpMethod.GET);

    GetClaimResponse result = service.getClaimDetails(GOOD_CLAIM_ID);
    assertResponseIsSuccess(result, HttpStatus.OK);
  }

  @Test
  public void testGetClaimDetails_404() throws Exception {
    String resp404Body = getTestData(CLAIM_RESPONSE_404);
    mockResponseForUrl(
        Mockito.doThrow(
            new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.name(),
                resp404Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(CLAIM_DETAILS, NOT_FOUND_CLAIM_ID),
        HttpMethod.GET);

    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class, () -> service.getClaimDetails(NOT_FOUND_CLAIM_ID));
    assertResponseHasMessageWithStatus(ex.getResponseBodyAsString(), HttpStatus.NOT_FOUND);
  }

  @Test
  public void testGetClaimDetailsDownstreamServerError_500() throws Exception {
    String resp500Body = getTestData(RESPONSE_500);
    mockResponseForUrl(
        Mockito.doThrow(
            new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                resp500Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(CLAIM_DETAILS, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.GET);

    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class,
            () -> service.getClaimDetails(INTERNAL_SERVER_ERROR_CLAIM_ID));
    assertResponseHasMessageWithStatus(
        ex.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetClaimDetailsInternalServerError_500() {
    mockResponseForUrl(
        Mockito.doThrow(new RuntimeException("nope")),
        formatClaimUrl(CLAIM_DETAILS, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.GET);

    BipException ex =
        Assertions.assertThrows(
            BipException.class, () -> service.getClaimDetails(INTERNAL_SERVER_ERROR_CLAIM_ID));
    assertSame(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
  }

  @Test
  public void testSetClaimToRfdStatus() {
    ResponseEntity<String> resp200 = ResponseEntity.ok("{}");
    ResponseEntity<String> resp500 =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
    mockResponseForUrl(
        Mockito.doReturn(resp200),
        formatClaimUrl(UPDATE_CLAIM_STATUS, GOOD_CLAIM_ID),
        HttpMethod.PUT);
    mockResponseForUrl(
        Mockito.doReturn(resp500),
        formatClaimUrl(UPDATE_CLAIM_STATUS, BAD_CLAIM_ID),
        HttpMethod.PUT);

    try {
      BipUpdateClaimResp result = service.setClaimToRfdStatus(GOOD_CLAIM_ID);
      assertEquals(result.getStatus(), 200);
    } catch (BipException e) {
      log.error("Positive setClaimToRfdStatus test failed.", e);
      fail();
    }

    try {
      service.setClaimToRfdStatus(BAD_CLAIM_ID);
      log.error("Negative setClaimToRfdStatus test failed.");
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
    }
  }

  @Test
  public void testSetClaimToRfdStatus_500() {
    String body = "{}";
    mockResponseForUrl(
        Mockito.doThrow(
            new HttpClientErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(UPDATE_CLAIM_STATUS, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.PUT);

    try {
      service.setClaimToRfdStatus(INTERNAL_SERVER_ERROR_CLAIM_ID);
      log.error("Negative updateClaimContention test failed.");
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
    }
  }

  @Test
  public void testGetClaimContention_200() throws Exception {
    String resp200Body = getTestData(CONTENTION_RESPONSE_200);
    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);

    mockResponseForUrl(
        Mockito.doReturn(resp200), formatClaimUrl(CONTENTION, GOOD_CLAIM_ID), HttpMethod.GET);

    GetClaimContentionsResponse result = service.getClaimContentions(GOOD_CLAIM_ID);
    assertResponseIsSuccess(result, HttpStatus.OK);
    assertEquals(1, result.getContentions().size());
  }

  @Test
  public void testGetClaimContentions_404() throws Exception {
    String resp404Body = getTestData(CLAIM_RESPONSE_404);

    mockResponseForUrl(
        Mockito.doThrow(
            new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.name(),
                resp404Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(CONTENTION, BAD_CLAIM_ID),
        HttpMethod.GET);

    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class, () -> service.getClaimContentions(BAD_CLAIM_ID));
    assertResponseHasMessageWithStatus(ex.getResponseBodyAsString(), HttpStatus.NOT_FOUND);
  }

  @Test
  public void testGetClaimContentionsDownstreamServerError_500() throws Exception {
    String resp500Body = getTestData(RESPONSE_500);
    mockResponseForUrl(
        Mockito.doThrow(
            new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                resp500Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(CONTENTION, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.GET);

    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class,
            () -> service.getClaimContentions(INTERNAL_SERVER_ERROR_CLAIM_ID));
    assertResponseHasMessageWithStatus(
        ex.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetClaimContentionsInternalServerError_500() {
    mockResponseForUrl(
        Mockito.doThrow(new RuntimeException("nope")),
        formatClaimUrl(CONTENTION, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.GET);

    BipException ex =
        Assertions.assertThrows(
            BipException.class, () -> service.getClaimContentions(INTERNAL_SERVER_ERROR_CLAIM_ID));
    assertSame(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
  }

  @Test
  public void testUpdateClaimContention() throws Exception {
    String resp200Body = getTestData(CONTENTION_RESPONSE_200);
    ResponseEntity<String> resp200 = ResponseEntity.ok(resp200Body);
    mockResponseForUrl(
        Mockito.doReturn(resp200), formatClaimUrl(CONTENTION, GOOD_CLAIM_ID), HttpMethod.PUT);

    UpdateClaimContentionsRequest request =
        UpdateClaimContentionsRequest.builder()
            .claimId(GOOD_CLAIM_ID)
            .updateContentions(
                List.of(
                    ExistingContention.builder()
                        .medicalInd(true)
                        .beginDate("2023-09-27T00:00:00-06:00")
                        .contentionTypeCode("NEW")
                        .claimantText("tendinitis/bilateral")
                        .build()))
            .build();

    UpdateClaimContentionsResponse response = service.updateClaimContentions(request);
    assertResponseIsSuccess(response, HttpStatus.OK);
  }

  @ParameterizedTest(name = "testUpdateClaimContentions_{0}")
  @EnumSource(TestCase.class)
  public void testUpdateClaimContentions_Non2xx(TestCase test) throws JsonProcessingException {
    mockResponseForUrl(
        Mockito.doThrow(test.ex), formatClaimUrl(CONTENTION, test.claimId), HttpMethod.PUT);
    UpdateClaimContentionsRequest request =
        UpdateClaimContentionsRequest.builder()
            .claimId(test.claimId)
            .updateContentions(
                List.of(
                    ExistingContention.builder()
                        .medicalInd(true)
                        .beginDate("2023-09-27T00:00:00-06:00")
                        .contentionTypeCode("NEW")
                        .claimantText("tendinitis/bilateral")
                        .build()))
            .build();
    HttpStatusCodeException ex =
        Assertions.assertThrows(test.ex.getClass(), () -> service.updateClaimContentions(request));
    assertResponseHasMessageWithStatus(ex.getResponseBodyAsString(), test.status);
  }

  @Test
  public void testUpdateClaimContentionsInternalServerError_500() {
    mockResponseForUrl(
        Mockito.doThrow(new RuntimeException("nope")),
        formatClaimUrl(CONTENTION, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.PUT);
    UpdateClaimContentionsRequest request =
        UpdateClaimContentionsRequest.builder()
            .claimId(INTERNAL_SERVER_ERROR_CLAIM_ID)
            .updateContentions(
                List.of(
                    ExistingContention.builder()
                        .medicalInd(true)
                        .beginDate("2023-09-27T00:00:00-06:00")
                        .contentionTypeCode("NEW")
                        .claimantText("tendinitis/bilateral")
                        .build()))
            .build();
    BipException ex =
        Assertions.assertThrows(BipException.class, () -> service.updateClaimContentions(request));
    assertSame(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
  }

  @Test
  public void testCancelClaim() {
    BipCloseClaimReason reason =
        BipCloseClaimReason.builder()
            .closeReasonText("because we are testing")
            .lifecycleStatusReasonCode("60")
            .build();
    ResponseEntity<String> resp200 = ResponseEntity.ok("{}");
    ResponseEntity<String> resp500 =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
    mockResponseForUrl(
        Mockito.doReturn(resp200), formatClaimUrl(CANCEL_CLAIM, GOOD_CLAIM_ID), HttpMethod.PUT);
    mockResponseForUrl(
        Mockito.doReturn(resp500), formatClaimUrl(CANCEL_CLAIM, BAD_CLAIM_ID), HttpMethod.PUT);

    try {
      var goodRequest =
          BipCloseClaimPayload.builder().claimId(GOOD_CLAIM_ID).reason(reason).build();
      BipCloseClaimResp result = service.cancelClaim(goodRequest);
      assertEquals(result.getStatus(), 200);
    } catch (BipException e) {
      log.error("Positive cancelClaim test failed.", e);
      fail();
    }

    try {
      var badRequest = BipCloseClaimPayload.builder().claimId(BAD_CLAIM_ID).reason(reason).build();
      BipCloseClaimResp responseNotFound = service.cancelClaim(badRequest);
      assertNull(responseNotFound);
      fail();
    } catch (BipException e) {
      assertSame(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
    }
  }

  @Test
  public void testPutTemporaryStationOfJurisdiction_200() {
    ResponseEntity<String> resp200 = ResponseEntity.ok("{}");

    mockResponseForUrl(
        Mockito.doReturn(resp200),
        formatClaimUrl(TEMP_STATION_OF_JURISDICTION, GOOD_CLAIM_ID),
        HttpMethod.PUT);

    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(GOOD_CLAIM_ID)
            .tempStationOfJurisdiction("398")
            .build();
    PutTempStationOfJurisdictionResponse result = service.putTempStationOfJurisdiction(request);
    assertResponseIsSuccess(result, HttpStatus.OK);
  }

  @Test
  public void testPutTemporaryStationOfJurisdiction_404() throws Exception {
    String resp404Body = getTestData(CLAIM_RESPONSE_404);

    mockResponseForUrl(
        Mockito.doThrow(
            new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.name(),
                resp404Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(TEMP_STATION_OF_JURISDICTION, BAD_CLAIM_ID),
        HttpMethod.PUT);

    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(BAD_CLAIM_ID)
            .tempStationOfJurisdiction("398")
            .build();
    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class, () -> service.putTempStationOfJurisdiction(request));
    assertResponseHasMessageWithStatus(ex.getResponseBodyAsString(), HttpStatus.NOT_FOUND);
  }

  @Test
  public void testPutTemporaryStationOfJurisdictionDownstreamServerError_500() throws Exception {
    String resp500Body = getTestData(RESPONSE_500);
    mockResponseForUrl(
        Mockito.doThrow(
            new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                resp500Body.getBytes(),
                Charset.defaultCharset())),
        formatClaimUrl(TEMP_STATION_OF_JURISDICTION, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.PUT);

    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(INTERNAL_SERVER_ERROR_CLAIM_ID)
            .tempStationOfJurisdiction("398")
            .build();
    HttpStatusCodeException ex =
        Assertions.assertThrows(
            HttpStatusCodeException.class, () -> service.putTempStationOfJurisdiction(request));
    assertResponseHasMessageWithStatus(
        ex.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testPutTemporaryStationOfJurisdictionInternalServerError_500() {
    mockResponseForUrl(
        Mockito.doThrow(new RuntimeException("nope")),
        formatClaimUrl(TEMP_STATION_OF_JURISDICTION, INTERNAL_SERVER_ERROR_CLAIM_ID),
        HttpMethod.PUT);

    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(INTERNAL_SERVER_ERROR_CLAIM_ID)
            .tempStationOfJurisdiction("398")
            .build();
    BipException ex =
        Assertions.assertThrows(
            BipException.class, () -> service.putTempStationOfJurisdiction(request));
    assertSame(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
  }

  @Test
  public void testIsApiFunctioning() {
    ResponseEntity<String> resp200 = ResponseEntity.ok(API_RESPONSE_200);

    String goodUrl = HTTPS + CLAIM_URL + SPECIAL_ISSUE_TYPES;

    mockResponseForUrl(Mockito.doReturn(resp200), goodUrl, HttpMethod.GET);
    try {
      assertTrue(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Positive testIsApiResponding test failed.", e);
      fail();
    }
    ResponseEntity<String> respEmpty = ResponseEntity.ok("");

    mockResponseForUrl(Mockito.doReturn(respEmpty), goodUrl, HttpMethod.GET);
    try {
      assertFalse(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Negative testIsApiResponding test failed.", e);
      fail();
    }
    mockResponseForUrl(
        Mockito.doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)),
        goodUrl,
        HttpMethod.GET);
    try {
      assertFalse(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Negative testIsApiResponding test failed.", e);
      fail();
    }
  }

  private void assertResponseIsSuccess(BipPayloadResponse response, HttpStatus status) {
    assertNotNull(response);
    assertEquals(status.value(), response.getStatusCode());
    assertEquals(status.name(), response.getStatusMessage());
    assertNull(response.getMessages());
  }

  private void assertResponseHasMessageWithStatus(String response, HttpStatus expected)
      throws JsonProcessingException {
    BipPayloadResponse bipResponse = MAPPER.readValue(response, BipPayloadResponse.class);
    assertNotNull(bipResponse.getMessages());
    assertEquals(1, bipResponse.getMessages().size());

    BipMessage message = bipResponse.getMessages().get(0);
    assertEquals(expected.value(), message.getStatus());
    assertEquals(expected.name(), message.getHttpStatus());
  }

  public enum TestCase {
    NOT_FOUND(NOT_FOUND_CLAIM_ID, HttpStatus.NOT_FOUND, CLAIM_RESPONSE_404),
    DOWNSTREAM_ERROR(
        INTERNAL_SERVER_ERROR_CLAIM_ID, HttpStatus.INTERNAL_SERVER_ERROR, RESPONSE_500);

    final long claimId;
    final HttpStatus status;
    final HttpStatusCodeException ex;

    @SneakyThrows
    TestCase(long claimId, HttpStatus status, String dataFile) {
      this.claimId = claimId;
      this.status = status;
      this.ex =
          status == HttpStatus.INTERNAL_SERVER_ERROR
              ? new HttpServerErrorException(
                  status, status.name(), getTestData(dataFile).getBytes(), Charset.defaultCharset())
              : new HttpClientErrorException(
                  status,
                  status.name(),
                  getTestData(dataFile).getBytes(),
                  Charset.defaultCharset());
    }
  }
}
