package gov.va.vro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tngtech.archunit.thirdparty.com.google.common.collect.ImmutableMap;
import gov.va.vro.model.claimmetrics.AssessmentInfo;
import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.ContentionInfo;
import gov.va.vro.model.claimmetrics.DocumentInfo;
import gov.va.vro.model.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClaimMetricsControllerTest extends BaseControllerTest {
  private static final AtomicInteger counter = new AtomicInteger(0);

  @MockBean private ClaimMetricsService service;

  @Autowired TestRestTemplate restTemplate;

  public ObjectMapper createObjectMapper() {
    return JsonMapper.builder().addModule(new JavaTimeModule()).build();
  }

  private final ObjectMapper mapper = createObjectMapper();

  // Generates a generic ClaimInfoResponse object.
  private ClaimInfoResponse generateClaimInfoResponse() {
    int index = counter.getAndIncrement();

    ClaimInfoResponse result = new ClaimInfoResponse();

    result.setClaimSubmissionId("claimSubmissionId_" + index);
    result.setIdType(Claim.V1_ID_TYPE);
    result.setVeteranIcn("icn_" + index);

    ContentionInfo contentionInfo = new ContentionInfo();
    contentionInfo.setDiagnosticCode("7101");

    Map<String, String> summary =
        ImmutableMap.of("ka" + index, "va" + index, "kb" + index, "vb" + index);

    AssessmentInfo assessmentInfo = new AssessmentInfo();
    assessmentInfo.setEvidenceInfo(summary);
    contentionInfo.setAssessments(Collections.singletonList(assessmentInfo));

    DocumentInfo documentInfo = new DocumentInfo();
    documentInfo.setDocumentName("documentName_" + index);
    documentInfo.setEvidenceInfo(summary);

    contentionInfo.setDocuments(Collections.singletonList(documentInfo));

    result.setContentions(Collections.singletonList(contentionInfo));
    return result;
  }

  // Generates a generic ClaimsInfo object.
  private ClaimsInfo generateClaimsInfo(int size) {
    ClaimsInfo result = new ClaimsInfo();
    List<ClaimInfoResponse> claimInfoResponses = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      claimInfoResponses.add(generateClaimInfoResponse());
    }
    result.setClaimInfoList(claimInfoResponses);
    int total = counter.getAndIncrement() + size + 1;
    result.setTotal(total);
    return result;
  }

  @Test
  void testClaimInfoAllUnAuthorized() {
    var responseEntity =
        restTemplate.exchange("/v2/claim-info", HttpMethod.GET, null, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
  }

  private HttpEntity<Void> getAuthorizationHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "test-key-01");
    return new HttpEntity<>(headers);
  }

  @Test
  void testClaimInfoAllWrongVerb() {
    HttpEntity<Void> requestEntity = getAuthorizationHeader();

    var responseEntity =
        restTemplate.exchange("/v2/claim-info", HttpMethod.POST, requestEntity, String.class);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
  }

  private ResponseEntity<String> callRestWithAuthorization(String uri) {
    HttpEntity<Void> requestEntity = getAuthorizationHeader();

    return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
  }

  @Test
  void testClaimInfoAllInvalidQueryParam() {
    ResponseEntity<String> re0 = callRestWithAuthorization("/v2/claim-info?size=0");
    assertEquals(HttpStatus.BAD_REQUEST, re0.getStatusCode());

    ResponseEntity<String> re1 = callRestWithAuthorization("/v2/claim-info?page=x");
    assertEquals(HttpStatus.BAD_REQUEST, re1.getStatusCode());

    ResponseEntity<String> re2 = callRestWithAuthorization("/v2/claim-info?page=-1");
    assertEquals(HttpStatus.BAD_REQUEST, re2.getStatusCode());
  }

  // Verifies happy path where service returns an expected object.
  @Test
  void testClaimInfoAll() throws JsonProcessingException {
    int size = 5;

    ClaimInfoQueryParams params = new ClaimInfoQueryParams(0, size, null);
    ClaimsInfo serviceOutput = generateClaimsInfo(size);

    // Return an expected exception if argument does not match.
    Mockito.when(service.findAllClaimInfo(ArgumentMatchers.any(ClaimInfoQueryParams.class)))
        .thenThrow(new IllegalStateException("Unexpected input to service."));
    Mockito.when(service.findAllClaimInfo(ArgumentMatchers.eq(params))).thenReturn(serviceOutput);

    ResponseEntity<String> responseEntity = callRestWithAuthorization("/v2/claim-info?size=5");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String body = responseEntity.getBody();
    assertNotNull(body);
    ClaimInfoResponse[] actual = mapper.readValue(body, ClaimInfoResponse[].class);
    assertNotNull(actual);
    assertEquals(size, actual.length);

    List<ClaimInfoResponse> responses = serviceOutput.getClaimInfoList();
    for (int index = 0; index < size; ++index) {
      assertEquals(responses.get(index), actual[index]);
    }
  }

  // Checks if a specific uri results in the expected argument to the service call.
  private void testClaimInfoAllQueryParamDefaults(String uri, ClaimInfoQueryParams expectedParams) {
    Mockito.reset(service);

    ArgumentCaptor<ClaimInfoQueryParams> captor =
        ArgumentCaptor.forClass(ClaimInfoQueryParams.class);

    callRestWithAuthorization(uri);

    Mockito.verify(service).findAllClaimInfo(captor.capture());
    ClaimInfoQueryParams actualParams = captor.getValue();

    assertEquals(expectedParams.getPage(), actualParams.getPage());
    assertEquals(expectedParams.getSize(), actualParams.getSize());
    assertEquals(expectedParams.getIcn(), actualParams.getIcn());
  }

  // Verifies default query parameters results in the expected argument to the service call.
  @Test
  void testClaimInfoAllQueryParamDefaults() {
    String uri0 = "/v2/claim-info";
    ClaimInfoQueryParams params0 = new ClaimInfoQueryParams(0, 10, null);
    testClaimInfoAllQueryParamDefaults(uri0, params0);

    String uri1 = "/v2/claim-info?size=15";
    ClaimInfoQueryParams params1 = new ClaimInfoQueryParams(0, 15, null);
    testClaimInfoAllQueryParamDefaults(uri1, params1);

    String uri2 = "/v2/claim-info?page=1";
    ClaimInfoQueryParams params2 = new ClaimInfoQueryParams(1, 10, null);
    testClaimInfoAllQueryParamDefaults(uri2, params2);

    String uri3 = "/v2/claim-info?page=1&size=15";
    ClaimInfoQueryParams params3 = new ClaimInfoQueryParams(1, 15, null);
    testClaimInfoAllQueryParamDefaults(uri3, params3);

    String uri4 = "/v2/claim-info?icn=12345";
    ClaimInfoQueryParams params4 = new ClaimInfoQueryParams(0, 10, "12345");
    testClaimInfoAllQueryParamDefaults(uri4, params4);

    String uri5 = "/v2/claim-info?page=2&size=16&icn=11145";
    ClaimInfoQueryParams params5 = new ClaimInfoQueryParams(2, 16, "11145");
    testClaimInfoAllQueryParamDefaults(uri5, params5);
  }

  // Verifies happy path where service returns an expected object.
  @Test
  void testClaimInfo() throws JsonProcessingException {
    ClaimInfoResponse claimInfo = generateClaimInfoResponse();

    String claimSubmissionId = claimInfo.getClaimSubmissionId();

    // Return an expected exception if argument does not match.
    Mockito.when(service.findClaimInfo(ArgumentMatchers.anyString(), ArgumentMatchers.isNull()))
        .thenThrow(new IllegalStateException("Unexpected input to service."));
    Mockito.when(
            service.findClaimInfo(
                ArgumentMatchers.eq(claimSubmissionId), ArgumentMatchers.anyString()))
        .thenReturn(claimInfo);

    String path = "/v2/claim-info/" + claimSubmissionId + "?claimVersion=v1";
    ResponseEntity<String> responseEntity = callRestWithAuthorization(path);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String body = responseEntity.getBody();
    assertNotNull(body);
    ClaimInfoResponse actual = mapper.readValue(body, ClaimInfoResponse.class);
    assertNotNull(actual);
    assertEquals(claimInfo, actual);
  }

  @Test
  void testClaimInfoNotValidId() {
    Mockito.when(service.findClaimInfo(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(null);

    String path = "/v2/claim-info/not_an_id/v1";
    ResponseEntity<String> responseEntity = callRestWithAuthorization(path);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  void testClaimMetrics() throws JsonProcessingException {
    ClaimMetricsResponse info = new ClaimMetricsResponse(5, 4, 3);

    Mockito.when(service.getClaimMetrics()).thenReturn(info);

    String path = "/v2/claim-metrics";
    ResponseEntity<String> responseEntity = callRestWithAuthorization(path);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String body = responseEntity.getBody();
    assertNotNull(body);
    ClaimMetricsResponse actual = mapper.readValue(body, ClaimMetricsResponse.class);
    assertNotNull(actual);
    assertEquals(info, actual);
  }

  // Generates a generic ClaimInfoResponse object.
  private ExamOrderInfoResponse generateExamOrderInfoResponse() {
    int index = counter.getAndIncrement();

    ExamOrderInfoResponse result = new ExamOrderInfoResponse();

    result.setCollectionId("collectionId_" + index);
    result.setOrderedAt(LocalDateTime.now());
    result.setStatus("DRAFT");
    result.setCreatedAt(LocalDateTime.now());
    result.setUpdatedAt(LocalDateTime.now());
    result.setHasAssociatedClaimSubmission(false);

    return result;
  }

  // Generates a generic ExamsOrdersInfo object.
  private ExamOrdersInfo generateExamOrdersInfo(int size) {
    ExamOrdersInfo result = new ExamOrdersInfo();
    List<ExamOrderInfoResponse> examOrderInfoResponses = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      examOrderInfoResponses.add(generateExamOrderInfoResponse());
    }
    result.setExamOrderInfoList(examOrderInfoResponses);
    int total = counter.getAndIncrement() + size + 1;
    result.setTotal(total);
    return result;
  }

  // Verifies happy path where service returns an expected object.
  @Test
  void testExamOrderInfo() throws JsonProcessingException {
    int size = 5;

    ExamOrderInfoQueryParams params = new ExamOrderInfoQueryParams(0, size, Boolean.FALSE);
    ExamOrdersInfo serviceOutput = generateExamOrdersInfo(size);

    // Return an expected exception if argument does not match.
    Mockito.when(service.findExamOrderInfo(ArgumentMatchers.any(ExamOrderInfoQueryParams.class)))
        .thenThrow(new IllegalStateException("Unexpected input to service."));
    Mockito.when(service.findExamOrderInfo(ArgumentMatchers.eq(params)))
        .thenReturn(serviceOutput);

    ResponseEntity<String> responseEntity = callRestWithAuthorization("/v2/exam-order-info?size=5");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String body = responseEntity.getBody();
    assertNotNull(body);
    ExamOrderInfoResponse[] actual = mapper.readValue(body, ExamOrderInfoResponse[].class);
    assertNotNull(actual);
    assertEquals(size, actual.length);

    List<ExamOrderInfoResponse> responses = serviceOutput.getExamOrderInfoList();
    for (int index = 0; index < size; ++index) {
      assertEquals(responses.get(index), actual[index]);
    }
  }

  // Checks if a specific uri results in the expected argument to the service call.
  private void testExamInfoQueryParamDefaults(
      String uri, ExamOrderInfoQueryParams expectedParams) {
    Mockito.reset(service);

    ArgumentCaptor<ExamOrderInfoQueryParams> captor =
        ArgumentCaptor.forClass(ExamOrderInfoQueryParams.class);

    callRestWithAuthorization(uri);

    Mockito.verify(service).findExamOrderInfo(captor.capture());
    ExamOrderInfoQueryParams actualParams = captor.getValue();

    assertEquals(expectedParams.getPage(), actualParams.getPage());
    assertEquals(expectedParams.getSize(), actualParams.getSize());
    assertEquals(expectedParams.getNotOrdered(), actualParams.getNotOrdered());
  }

  // Verifies default query parameters results in the expected argument to the service call.
  @Test
  void testExamInfoAllQueryParamDefaults() {
    String uri0 = "/v2/exam-order-info";
    ExamOrderInfoQueryParams params0 = new ExamOrderInfoQueryParams(0, 10, Boolean.FALSE);
    testExamInfoQueryParamDefaults(uri0, params0);

    String uri1 = "/v2/exam-order-info?size=15";
    ExamOrderInfoQueryParams params1 = new ExamOrderInfoQueryParams(0, 15, Boolean.FALSE);
    testExamInfoQueryParamDefaults(uri1, params1);

    String uri2 = "/v2/exam-order-info?page=1";
    ExamOrderInfoQueryParams params2 = new ExamOrderInfoQueryParams(1, 10, Boolean.FALSE);
    testExamInfoQueryParamDefaults(uri2, params2);

    String uri3 = "/v2/exam-order-info?page=1&size=15";
    ExamOrderInfoQueryParams params3 = new ExamOrderInfoQueryParams(1, 15, Boolean.FALSE);
    testExamInfoQueryParamDefaults(uri3, params3);
  }

  @Test
  void testExamOrderInfoAllInvalidQueryParam() {
    ResponseEntity<String> re0 = callRestWithAuthorization("/v2/exam-order-info?size=0");
    assertEquals(HttpStatus.BAD_REQUEST, re0.getStatusCode());

    ResponseEntity<String> re1 = callRestWithAuthorization("/v2/exam-order-info?page=x");
    assertEquals(HttpStatus.BAD_REQUEST, re1.getStatusCode());

    ResponseEntity<String> re2 = callRestWithAuthorization("/v2/exam-order-info?page=-1");
    assertEquals(HttpStatus.BAD_REQUEST, re2.getStatusCode());
  }
}
