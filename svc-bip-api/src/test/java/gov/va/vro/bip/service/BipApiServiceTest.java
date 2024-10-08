package gov.va.vro.bip.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.config.JacksonConfig;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimRequest;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.Contention;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.ExistingContention;
import gov.va.vro.bip.model.contentions.GetClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesRequest;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleRequest;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.metricslogging.MetricLoggerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentMatchers;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BipApiServiceTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final long GOOD_CLAIM_ID = 9666959L;
  private static final long NOT_FOUND_CLAIM_ID = 9234567L;
  private static final long INTERNAL_SERVER_ERROR_CLAIM_ID = 9345678L;
  private static final String RESPONSE_500 = "bip-test-data/response_500.json";
  private static final String CONTENTION_RESPONSE_200 =
      "bip-test-data/contention_response_200.json";
  private static final String CONTENTION_RESPONSE_201 =
      "bip-test-data/contention_response_201.json";
  private static final String CLAIM_RESPONSE_404 = "bip-test-data/claim_response_404.json";
  private static final String CLAIM_RESPONSE_200 = "bip-test-data/claim_response_200.json";
  private static final String CLAIM_DETAILS = "/claims/%s";
  private static final String CLAIM_LIFECYCLE_STATUS = "/claims/%s/lifecycle_status";
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
  private static final String API_RESPONSE_200 = "{\"mock response\"}";
  private static final String SPECIAL_ISSUE_TYPES = "/contentions/special_issue_types";
  private static final String SPECIAL_ISSUE_TYPES_RESPONSE_200 =
      "bip-test-data/special_issue_types_response_200.json";

  private BipApiService service;

  private final ObjectMapper mapper = new JacksonConfig().objectMapper();

  @Mock private MetricLoggerService metricLoggerService;

  @Mock private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    BipApiProps bipApiProps = new BipApiProps();
    bipApiProps.setClaimBaseUrl(CLAIM_URL);
    bipApiProps.setClaimIssuer(CLAIM_ISSUER);
    bipApiProps.setClaimSecret(CLAIM_SECRET);
    bipApiProps.setApplicationId(APP_ID);
    bipApiProps.setStationId(STATION_ID);
    bipApiProps.setClaimClientId(CLAIM_USERID);

    service = new BipApiService(restTemplate, bipApiProps, mapper, metricLoggerService);
  }

  private String formatClaimUrl(String format, Long claimId) {
    String baseUrl = HTTPS + CLAIM_URL;
    return baseUrl + String.format(format, claimId);
  }

  private String formatUrl(String url) {
    return HTTPS + CLAIM_URL + url;
  }

  private static String getTestData(String dataFile) throws Exception {
    String filename =
        Objects.requireNonNull(BipApiServiceTest.class.getClassLoader().getResource(dataFile))
            .getPath();
    Path filePath = Path.of(filename);
    return Files.readString(filePath);
  }

  private void mockResponseForUrl(
      Stubber response,
      String claimUrl,
      HttpMethod httpMethod,
      Class<? extends BipPayloadResponse> clazz) {
    response
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(claimUrl),
            ArgumentMatchers.eq(httpMethod),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(clazz));
  }

  private void mockResponseForUrl(Stubber response, String claimUrl) {
    response
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.eq(claimUrl),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
  }

  private <T extends BipPayloadResponse> void mock2xxResponse(
      HttpMethod httpMethod,
      String urlFormat,
      HttpStatus httpStatus,
      Class<T> responseType,
      String responseFile)
      throws Exception {
    ResponseEntity<T> responseEntity;
    if (Objects.isNull(responseFile)) {
      responseEntity = new ResponseEntity<>(httpStatus);
    } else {
      T body = mapper.readValue(getTestData(responseFile), responseType);
      responseEntity = new ResponseEntity<>(body, httpStatus);
    }
    mockResponseForUrl(
        Mockito.doReturn(responseEntity),
        formatClaimUrl(urlFormat, BipApiServiceTest.GOOD_CLAIM_ID),
        httpMethod,
        responseType);
  }

  private <T extends BipPayloadResponse> void mockExceptionResponse(
      Exception exception, String url, HttpMethod httpMethod, Class<T> responseType) {
    mockResponseForUrl(Mockito.doThrow(exception), url, httpMethod, responseType);
  }

  @Nested
  public class GetClaimDetails {
    @Test
    public void testGetClaimDetails_200() throws Exception {
      mock2xxResponse(
          HttpMethod.GET, CLAIM_DETAILS, HttpStatus.OK, GetClaimResponse.class, CLAIM_RESPONSE_200);

      GetClaimRequest request = GetClaimRequest.builder().claimId(GOOD_CLAIM_ID).build();
      GetClaimResponse result = service.getClaimDetails(request);
      assertResponseIsSuccess(result, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testGetClaimDetails_{0}")
    @EnumSource(TestCase.class)
    public void testGetClaimDetails_non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CLAIM_DETAILS, test.claimId),
          HttpMethod.GET,
          GetClaimResponse.class);

      GetClaimRequest request = GetClaimRequest.builder().claimId(test.claimId).build();
      Exception ex =
          Assertions.assertThrows(test.ex.getClass(), () -> service.getClaimDetails(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class GetClaimContentions {
    @Test
    public void testGetClaimContention_200() throws Exception {
      mock2xxResponse(
          HttpMethod.GET,
          CONTENTION,
          HttpStatus.OK,
          GetClaimContentionsResponse.class,
          CONTENTION_RESPONSE_200);

      GetClaimContentionsRequest request =
          GetClaimContentionsRequest.builder().claimId(GOOD_CLAIM_ID).build();
      GetClaimContentionsResponse result = service.getClaimContentions(request);
      assertResponseIsSuccess(result, HttpStatus.OK);
      assertEquals(1, result.getContentions().size());
      verifyMetricsAreLogged();
    }

    @Test
    public void testGetClaimContention_204() throws Exception {
      mock2xxResponse(
          HttpMethod.GET,
          CONTENTION,
          HttpStatus.NO_CONTENT,
          GetClaimContentionsResponse.class,
          null);

      GetClaimContentionsRequest request =
          GetClaimContentionsRequest.builder().claimId(GOOD_CLAIM_ID).build();
      GetClaimContentionsResponse result = service.getClaimContentions(request);
      assertResponseIsSuccess(result, HttpStatus.NO_CONTENT);
      assertNull(result.getContentions());
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testGetClaimContentions_{0}")
    @EnumSource(TestCase.class)
    public void testGetClaimContentions_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CONTENTION, test.claimId),
          HttpMethod.GET,
          GetClaimContentionsResponse.class);

      GetClaimContentionsRequest request =
          GetClaimContentionsRequest.builder().claimId(test.claimId).build();
      Exception ex =
          Assertions.assertThrows(test.ex.getClass(), () -> service.getClaimContentions(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class CreateClaimContentions {
    @Test
    public void testCreateClaimContentions_201() throws Exception {
      mock2xxResponse(
          HttpMethod.POST,
          CONTENTION,
          HttpStatus.CREATED,
          CreateClaimContentionsResponse.class,
          CONTENTION_RESPONSE_201);
      CreateClaimContentionsRequest request =
          CreateClaimContentionsRequest.builder()
              .claimId(GOOD_CLAIM_ID)
              .createContentions(
                  List.of(
                      Contention.builder()
                          .medicalInd(true)
                          .beginDate(OffsetDateTime.parse("2023-09-27T00:00:00-06:00"))
                          .contentionTypeCode("NEW")
                          .claimantText("tendinitis/bilateral")
                          .build()))
              .build();
      CreateClaimContentionsResponse response = service.createClaimContentions(request);
      assertResponseIsSuccess(response, HttpStatus.CREATED);
      assertNotNull(response.getContentionIds());
      assertEquals(1, response.getContentionIds().size());
      assertEquals(1, response.getContentionIds().get(0));
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testCreateClaimContentions_{0}")
    @EnumSource(TestCase.class)
    public void testCreateClaimContentions_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CONTENTION, test.claimId),
          HttpMethod.POST,
          CreateClaimContentionsResponse.class);
      CreateClaimContentionsRequest request =
          CreateClaimContentionsRequest.builder()
              .claimId(test.claimId)
              .createContentions(
                  List.of(
                      Contention.builder()
                          .medicalInd(true)
                          .beginDate(OffsetDateTime.parse("2023-09-27T00:00:00-06:00"))
                          .contentionTypeCode("NEW")
                          .claimantText("tendinitis/bilateral")
                          .build()))
              .build();
      Exception ex =
          Assertions.assertThrows(
              test.ex.getClass(), () -> service.createClaimContentions(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class UpdateClaimContentions {
    @Test
    public void testUpdateClaimContentions_200() throws Exception {
      mock2xxResponse(
          HttpMethod.PUT,
          CONTENTION,
          HttpStatus.OK,
          UpdateClaimContentionsResponse.class,
          CONTENTION_RESPONSE_200);
      UpdateClaimContentionsRequest request =
          UpdateClaimContentionsRequest.builder()
              .claimId(GOOD_CLAIM_ID)
              .updateContentions(
                  List.of(
                      ExistingContention.builder()
                          .medicalInd(true)
                          .beginDate(OffsetDateTime.parse("2023-09-27T00:00:00-06:00"))
                          .contentionTypeCode("NEW")
                          .claimantText("tendinitis/bilateral")
                          .build()))
              .build();

      UpdateClaimContentionsResponse response = service.updateClaimContentions(request);
      assertResponseIsSuccess(response, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testUpdateClaimContentions_{0}")
    @EnumSource(TestCase.class)
    public void testUpdateClaimContentions_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CONTENTION, test.claimId),
          HttpMethod.PUT,
          UpdateClaimContentionsResponse.class);
      UpdateClaimContentionsRequest request =
          UpdateClaimContentionsRequest.builder()
              .claimId(test.claimId)
              .updateContentions(
                  List.of(
                      ExistingContention.builder()
                          .medicalInd(true)
                          .beginDate(OffsetDateTime.parse("2023-09-27T00:00:00-06:00"))
                          .contentionTypeCode("NEW")
                          .claimantText("tendinitis/bilateral")
                          .build()))
              .build();
      Exception ex =
          Assertions.assertThrows(
              test.ex.getClass(), () -> service.updateClaimContentions(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class CancelClaim {
    @Test
    public void testCancelClaim_200() throws Exception {
      mock2xxResponse(HttpMethod.PUT, CANCEL_CLAIM, HttpStatus.OK, CancelClaimResponse.class, null);
      CancelClaimRequest request =
          CancelClaimRequest.builder()
              .claimId(GOOD_CLAIM_ID)
              .closeReasonText("because we are testing")
              .lifecycleStatusReasonCode("60")
              .build();
      CancelClaimResponse response = service.cancelClaim(request);
      assertResponseIsSuccess(response, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testCancelClaim_{0}")
    @EnumSource(TestCase.class)
    public void testCancelClaim_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CANCEL_CLAIM, test.claimId),
          HttpMethod.PUT,
          CancelClaimResponse.class);
      CancelClaimRequest request =
          CancelClaimRequest.builder()
              .claimId(test.claimId)
              .closeReasonText("because we are testing")
              .lifecycleStatusReasonCode("60")
              .build();
      Exception ex =
          Assertions.assertThrows(test.ex.getClass(), () -> service.cancelClaim(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class PutLifecycleStatus {
    @Test
    public void testPutLifecycleStatus_200() throws Exception {
      mock2xxResponse(
          HttpMethod.PUT,
          CLAIM_LIFECYCLE_STATUS,
          HttpStatus.OK,
          PutClaimLifecycleResponse.class,
          null);

      PutClaimLifecycleRequest request =
          PutClaimLifecycleRequest.builder()
              .claimId(GOOD_CLAIM_ID)
              .claimLifecycleStatus("Just a test")
              .build();
      PutClaimLifecycleResponse response = service.putClaimLifecycleStatus(request);
      assertResponseIsSuccess(response, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testPutLifecycleStatus_{0}")
    @EnumSource(TestCase.class)
    public void testPutLifecycleStatus_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(CLAIM_LIFECYCLE_STATUS, test.claimId),
          HttpMethod.PUT,
          PutClaimLifecycleResponse.class);
      PutClaimLifecycleRequest request =
          PutClaimLifecycleRequest.builder()
              .claimId(test.claimId)
              .claimLifecycleStatus("Just a test")
              .build();
      Exception ex =
          Assertions.assertThrows(
              test.ex.getClass(), () -> service.putClaimLifecycleStatus(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class PutTemporaryStationOfJurisdiction {
    @ParameterizedTest
    @NullAndEmptySource
    @CsvSource(value = {"398"})
    public void testPutTemporaryStationOfJurisdiction_200(String tsoj) throws Exception {
      mock2xxResponse(
          HttpMethod.PUT,
          TEMP_STATION_OF_JURISDICTION,
          HttpStatus.OK,
          PutTempStationOfJurisdictionResponse.class,
          null);

      PutTempStationOfJurisdictionRequest request =
          PutTempStationOfJurisdictionRequest.builder()
              .claimId(GOOD_CLAIM_ID)
              .tempStationOfJurisdiction(tsoj)
              .build();
      PutTempStationOfJurisdictionResponse result = service.putTempStationOfJurisdiction(request);
      assertResponseIsSuccess(result, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testPutTemporaryStationOfJurisdiction_{0}")
    @EnumSource(TestCase.class)
    public void testPutTemporaryStationOfJurisdiction_Non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatClaimUrl(TEMP_STATION_OF_JURISDICTION, test.claimId),
          HttpMethod.PUT,
          PutTempStationOfJurisdictionResponse.class);

      PutTempStationOfJurisdictionRequest request =
          PutTempStationOfJurisdictionRequest.builder()
              .claimId(test.claimId)
              .tempStationOfJurisdiction("398")
              .build();
      Exception ex =
          Assertions.assertThrows(
              test.ex.getClass(), () -> service.putTempStationOfJurisdiction(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Nested
  public class GetSpecialIssueTypes {
    @Test
    public void testGetSpecialIssueTypes_200() throws Exception {
      mock2xxResponse(
          HttpMethod.GET,
          SPECIAL_ISSUE_TYPES,
          HttpStatus.OK,
          GetSpecialIssueTypesResponse.class,
          SPECIAL_ISSUE_TYPES_RESPONSE_200);

      GetSpecialIssueTypesRequest request = GetSpecialIssueTypesRequest.builder().build();
      GetSpecialIssueTypesResponse result = service.getSpecialIssueTypes(request);
      assertResponseIsSuccess(result, HttpStatus.OK);
      verifyMetricsAreLogged();
    }

    @ParameterizedTest(name = "testGetSpecialIssueTypes_{0}")
    @EnumSource(
        value = TestCase.class,
        names = {"DOWNSTREAM_ERROR", "BIP_INTERNAL"})
    public void testGetSpecialIssueTypes_non2xx(TestCase test) throws Exception {
      mockExceptionResponse(
          test.ex,
          formatUrl(SPECIAL_ISSUE_TYPES),
          HttpMethod.GET,
          GetSpecialIssueTypesResponse.class);

      GetSpecialIssueTypesRequest request = GetSpecialIssueTypesRequest.builder().build();
      Exception ex =
          Assertions.assertThrows(test.ex.getClass(), () -> service.getSpecialIssueTypes(request));
      assertResponseExceptionWithStatus(ex, test.status);
      verifyMetricIsLoggedForExceptions(test);
    }
  }

  @Test
  public void testIsApiFunctioning() {
    ResponseEntity<String> resp200 = ResponseEntity.ok(API_RESPONSE_200);

    String goodUrl = HTTPS + CLAIM_URL + SPECIAL_ISSUE_TYPES;

    mockResponseForUrl(Mockito.doReturn(resp200), goodUrl);
    try {
      assertTrue(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Positive testIsApiResponding test failed.", e);
      fail();
    }
    ResponseEntity<String> respEmpty = ResponseEntity.ok("");

    mockResponseForUrl(Mockito.doReturn(respEmpty), goodUrl);
    try {
      assertFalse(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Negative testIsApiResponding test failed.", e);
      fail();
    }
    mockResponseForUrl(
        Mockito.doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)), goodUrl);
    try {
      assertFalse(service.isApiFunctioning());
    } catch (BipException e) {
      log.error("Negative testIsApiResponding test failed.", e);
      fail();
    }
  }

  @Test
  public void testMetricsUsesBipPrefix() throws Exception {

    mock2xxResponse(
        HttpMethod.PUT,
        TEMP_STATION_OF_JURISDICTION,
        HttpStatus.OK,
        PutTempStationOfJurisdictionResponse.class,
        null);

    PutTempStationOfJurisdictionRequest request =
        PutTempStationOfJurisdictionRequest.builder()
            .claimId(GOOD_CLAIM_ID)
            .tempStationOfJurisdiction("lorem")
            .build();

    PutTempStationOfJurisdictionResponse result = service.putTempStationOfJurisdiction(request);
    assertResponseIsSuccess(result, HttpStatus.OK);
    verify(metricLoggerService, times(1))
        .submitCount(
            IMetricLoggerService.METRIC.REQUEST_START,
            new String[] {
              "expectedResponse:PutTempStationOfJurisdictionResponse",
              "source:bipApiService",
              "method:PUT"
            });
    verify(metricLoggerService, times(1))
        .submitRequestDuration(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    verify(metricLoggerService, times(1))
        .submitCount(
            IMetricLoggerService.METRIC.RESPONSE_COMPLETE,
            new String[] {
              "expectedResponse:PutTempStationOfJurisdictionResponse",
              "source:bipApiService",
              "method:PUT"
            });
  }

  private void assertResponseIsSuccess(BipPayloadResponse response, HttpStatus status) {
    assertNotNull(response);
    assertEquals(status.value(), response.getStatusCode());
    assertEquals(status.name(), response.getStatusMessage());
    assertNull(response.getMessages());
  }

  private void verifyMetricsAreLogged() {
    verify(metricLoggerService, times(2))
        .submitCount(ArgumentMatchers.any(), ArgumentMatchers.any(String[].class));
    verify(metricLoggerService, times(1))
        .submitRequestDuration(
            ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong(),
            ArgumentMatchers.any(String[].class));
  }

  private void verifyMetricIsLoggedForExceptions(TestCase testCase) {
    verify(metricLoggerService, times(testCase == TestCase.BIP_INTERNAL ? 2 : 1))
        .submitCount(ArgumentMatchers.any(), ArgumentMatchers.any(String[].class));
  }

  private void assertResponseExceptionWithStatus(Exception ex, HttpStatus expected)
      throws JsonProcessingException {
    if (ex instanceof HttpStatusCodeException httpStatusCodeException) {
      assertResponseHasMessageWithStatus(
          httpStatusCodeException.getResponseBodyAsString(), expected);
    } else if (ex instanceof BipException bipException) {
      assertSame(expected, bipException.getStatus());
    } else {
      fail("Unsupported Error Type");
    }
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

  private enum ErrType {
    CLIENT,
    SERVER,
    INTERNAL
  }

  public enum TestCase {
    NOT_FOUND(ErrType.CLIENT, NOT_FOUND_CLAIM_ID, HttpStatus.NOT_FOUND, CLAIM_RESPONSE_404),
    DOWNSTREAM_ERROR(
        ErrType.SERVER,
        INTERNAL_SERVER_ERROR_CLAIM_ID,
        HttpStatus.INTERNAL_SERVER_ERROR,
        RESPONSE_500),
    BIP_INTERNAL(
        ErrType.INTERNAL,
        INTERNAL_SERVER_ERROR_CLAIM_ID,
        HttpStatus.INTERNAL_SERVER_ERROR,
        RESPONSE_500);

    final long claimId;
    final HttpStatus status;
    final Exception ex;

    @SneakyThrows
    TestCase(ErrType type, long claimId, HttpStatus status, String dataFile) {
      this.claimId = claimId;
      this.status = status;
      this.ex =
          switch (type) {
            case CLIENT -> new HttpClientErrorException(
                status, status.name(), getTestData(dataFile).getBytes(), Charset.defaultCharset());
            case SERVER -> new HttpServerErrorException(
                status, status.name(), getTestData(dataFile).getBytes(), Charset.defaultCharset());
            case INTERNAL -> new RuntimeException("nope");
          };
    }
  }
}
