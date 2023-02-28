package gov.va.vro.controller;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.config.AppTestConfig;
import gov.va.vro.config.AppTestUtil;
import gov.va.vro.controller.exception.ClaimProcessingError;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.spi.model.Claim;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.Builder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(AppTestConfig.class)
@CamelSpringBootTest
class VroControllerTest extends BaseControllerTest {

  @Autowired private AppTestUtil util;

  @Autowired protected CamelContext camelContext;

  @EndpointInject("mock:claim-submit-full")
  private MockEndpoint mockFullHealthEndpoint;

  @EndpointInject("mock:generate-pdf")
  private MockEndpoint mockGeneratePdfEndpoint;

  @EndpointInject("mock:fetch-pdf")
  private MockEndpoint mockFetchPdfEndpoint;

  @Value("classpath:test-data/pdf-generator-input-01.json")
  private Resource pdfGeneratorInput01;

  @Test
  @DirtiesContext
  void postFullHealthAssessment() throws Exception {

    // intercept the original endpoint, skip it and replace it with the mock
    // endpoint
    adviceWith(
        camelContext,
        "claim-submit",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:claim-submit-exchange"
                        + "?queue=claim-submit"
                        + "&routingKey=code.hypertension&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit"));
    // Mock secondary process endpoint
    adviceWith(
        camelContext,
        "claim-submit-full",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:health-assess-exchange"
                        + "?routingKey=health-assess.hypertension&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit-full"));
    // The mock endpoint returns a valid response
    mockFullHealthEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<Claim, String>fromFunction(
            claim -> util.claimToResponse(claim, true, null)));

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("7101");

    var responseEntity1 =
        post("/v1/full-health-data-assessment", request, FullHealthDataAssessmentResponse.class);
    assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
    FullHealthDataAssessmentResponse response1 = responseEntity1.getBody();
    assertNotNull(response1);
    assertEquals(request.getDiagnosticCode(), response1.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response1.getVeteranIcn());

    // Now submit an existing claim:
    var responseEntity2 =
        post("/v1/full-health-data-assessment", request, FullHealthDataAssessmentResponse.class);
    assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
    FullHealthDataAssessmentResponse response2 = responseEntity2.getBody();
    assertNotNull(response2);
    assertEquals(request.getDiagnosticCode(), response2.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response2.getVeteranIcn());

    var claimSubmission =
        claimSubmissionRepository.findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(
            request.getClaimSubmissionId(), Claim.V1_ID_TYPE);
    assertTrue(claimSubmission.isPresent());
    var claim = claimSubmission.get().getClaim();
    assertNull(claim.getVbmsId());
    assertEquals(2, claim.getClaimSubmissions().size());
  }

  @Test
  @DirtiesContext
  void fullHealthAssessmentMissingEvidence() throws Exception {
    adviceWith(
        camelContext,
        "claim-submit",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:claim-submit-exchange"
                        + "?queue=claim-submit"
                        + "&routingKey=code.hypertension&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit"));
    // Mock secondary process endpoint
    adviceWith(
        camelContext,
        "claim-submit-full",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:health-assess-exchange"
                        + "?routingKey=health-assess.hypertension&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit-full"));

    mockFullHealthEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<Claim, String>fromFunction(
            claim ->
                util.claimToResponse(claim, false, "Internal error while processing claim data.")));
    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("7101");

    var responseEntity = post("/v2/health-data-assessment", request, ClaimProcessingError.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    var claimProcessingError = responseEntity.getBody();
    assertNotNull(claimProcessingError);
    assertEquals("Internal error while processing claim data.", claimProcessingError.getMessage());
    assertEquals("1234", claimProcessingError.getClaimSubmissionId());
  }

  @Test
  void fullHealthAssessmentInvalidInput() {
    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setVeteranIcn("icn");

    var responseEntity = post("/v2/health-data-assessment", request, ClaimProcessingError.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    var claimProcessingError = responseEntity.getBody();
    assertNotNull(claimProcessingError);
    String[] actual = claimProcessingError.getMessage().split("\n");
    Arrays.sort(actual);
    String[] expected = {
      "claimSubmissionId: Claim submission id cannot be empty",
      "diagnosticCode: Diagnostic code cannot be empty"
    };
    assertArrayEquals(expected, actual);
  }

  @Test
  void fullHealthAssessmentMalformedJson() {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    headers.put("content-type", "application/json");
    var responseEntity =
        post(
            "/v2/health-data-assessment",
            "{ \"one\":\"one\", \"two\":\"two\",}",
            headers,
            ClaimProcessingError.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  void serverResponseUnsupportedHttpMethod() {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    headers.put("content-type", "application/json");
    String url = "/v2/claim-metrics";
    String sampleRequestBody = "{ \"one\":\"one\", \"two\":\"two\",}";
    var getResponseEntity = get(url, headers, ClaimProcessingError.class);
    var postResponseEntity = post(url, sampleRequestBody, headers, ClaimProcessingError.class);
    var putResponseEntity = put(url, sampleRequestBody, headers, ClaimProcessingError.class);
    assertEquals(HttpStatus.OK, getResponseEntity.getStatusCode());
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, postResponseEntity.getStatusCode());
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, putResponseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  void generatePdf() throws Exception {
    var mapper = new ObjectMapper();
    var mockResponseObj = new GeneratePdfResponse("1234", "7701", "COMPLETE");
    String mockResponse = mapper.writeValueAsString(mockResponseObj);
    adviceWith(
        camelContext,
        "generate-pdf",
        route ->
            route
                .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_GENERATE_PDF)
                .skipSendToOriginalEndpoint()
                .setBody(Builder.simple(mockResponse))
                .to("mock:generate-pdf"));
    mockGeneratePdfEndpoint.expectedMessageCount(1);

    InputStream stream = pdfGeneratorInput01.getInputStream();
    String inputAsString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    GeneratePdfRequest input = mapper.readValue(inputAsString, GeneratePdfRequest.class);
    var response = post("/v1/evidence-pdf", input, GeneratePdfResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void generatePdfInvalidInput() {
    var generatePdf = new GeneratePdfRequest();
    generatePdf.setClaimSubmissionId("1234");
    generatePdf.setPdfLibrary("wkhtmltopdf");
    generatePdf.setVeteranInfo(new VeteranInfo());
    generatePdf.setEvidence(new AbdEvidence());
    var response = post("/v1/evidence-pdf", generatePdf, ClaimProcessingError.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(response.getBody().getMessage(), "diagnosticCode: must not be blank");
  }

  @Test
  @DirtiesContext
  void fetchPdf() throws Exception {
    adviceWith(
        camelContext,
        "fetch-pdf",
        route ->
            route
                .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_FETCH_PDF)
                .skipSendToOriginalEndpoint()
                .to("mock:fetch-pdf"));
    mockFetchPdfEndpoint.expectedMessageCount(1);

    var fetchPdfResponse = new FetchPdfResponse("1234", "ERROR", "diagnosis", null, "");

    mockFetchPdfEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.fromFunction(
            (Function<Object, Object>) o -> util.toJsonString(fetchPdfResponse)));

    var response = get("/v1/evidence-pdf/1234", null, String.class);
    assertNotNull(response);
  }

  @Test
  @DirtiesContext
  void fetchPdfAcceptJson() throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    fetchPdfCommon(headers);
  }

  @Test
  @DirtiesContext
  void fetchPdfAcceptPdf() throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/pdf");
    fetchPdfCommon(headers);
  }

  void fetchPdfCommon(Map<String, String> headers) throws Exception {
    adviceWith(
        camelContext,
        "fetch-pdf",
        route ->
            route
                .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_FETCH_PDF)
                .skipSendToOriginalEndpoint()
                .to("mock:fetch-pdf"));
    mockFetchPdfEndpoint.expectedMessageCount(1);

    var fetchPdfResponse = new FetchPdfResponse();
    fetchPdfResponse.setPdfData(Base64.getEncoder().encodeToString("Example PDF".getBytes()));
    fetchPdfResponse.setClaimSubmissionId("1239");
    fetchPdfResponse.setStatus("SUCCESS");

    mockFetchPdfEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.fromFunction(
            (Function<Object, Object>) o -> util.toJsonString(fetchPdfResponse)));

    var response = get("/v1/evidence-pdf/1239", null, headers, byte[].class);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    String value = new String(response.getBody());
    assertEquals("Example PDF", value);
  }
}
