package gov.va.vro;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.model.AbdEvidence;
import gov.va.vro.api.model.VeteranInfo;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FetchPdfResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.controller.exception.ClaimProcessingError;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.provider.camel.FunctionProcessor;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.spi.model.Claim;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.function.Function;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@CamelSpringBootTest
class VroControllerTest extends BaseIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private ClaimRepository claimRepository;

  @Autowired protected CamelContext camelContext;

  @EndpointInject("mock:claim-submit")
  private MockEndpoint mockSubmitClaimEndpoint;

  @EndpointInject("mock:generate-pdf")
  private MockEndpoint mockGeneratePdfEndpoint;

  @EndpointInject("mock:fetch-pdf")
  private MockEndpoint mockFetchPdfEndpoint;

  @Test
  void postHealthAssessment() throws Exception {

    // intercept the original endpoint, skip it and replace it with the mock endpoint
    adviceWith(
        camelContext,
        "claim-submit",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:claim-submit-exchange"
                        + "?queue=claim-submit"
                        + "&routingKey=code.1701&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit"));
    // The mock endpoint returns a valid response
    mockSubmitClaimEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<Claim, String>fromFunction(claim -> claimToResponse(claim, true)));

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    var responseEntity1 =
        post("/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);

    assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
    HealthDataAssessmentResponse response1 = responseEntity1.getBody();
    assertEquals(request.getDiagnosticCode(), response1.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response1.getVeteranIcn());

    // Now submit an existing claim:
    var responseEntity2 =
        post("/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);
    assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
    HealthDataAssessmentResponse response2 = responseEntity2.getBody();
    assertEquals(request.getDiagnosticCode(), response2.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response2.getVeteranIcn());

    // side effect: claim is created in the DB
    Optional<ClaimEntity> claimEntityOptional =
        claimRepository.findByClaimSubmissionIdAndIdType("1234", "va.gov-Form526Submission");
    assertTrue(claimEntityOptional.isPresent());
  }

  @Test
  void postHealthAssessment_missing_evidence() throws Exception {
    adviceWith(
        camelContext,
        "claim-submit",
        route ->
            route
                .interceptSendToEndpoint(
                    "rabbitmq:claim-submit-exchange"
                        + "?queue=claim-submit"
                        + "&routingKey=code.1701&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit"));

    mockSubmitClaimEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<Claim, String>fromFunction(claim -> claimToResponse(claim, false)));

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    var responseEntity = post("/v1/health-data-assessment", request, ClaimProcessingError.class);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    var claimProcessingError = responseEntity.getBody();
    assertEquals("No evidence found.", claimProcessingError.getMessage());
    assertEquals("1234", claimProcessingError.getClaimSubmissionId());
  }

  @Test
  void generatePdf() throws Exception {
    adviceWith(
        camelContext,
        "generate-pdf",
        route ->
            route
                .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_GENERATE_PDF)
                .skipSendToOriginalEndpoint()
                .to("mock:generate-pdf"));
    mockGeneratePdfEndpoint.expectedMessageCount(1);

    var generatePdf = new GeneratePdfRequest();
    generatePdf.setClaimSubmissionId("1234");
    generatePdf.setDiagnosticCode("1234");
    generatePdf.setVeteranInfo(new VeteranInfo());
    generatePdf.setEvidence(new AbdEvidence());
    var response = post("/v1/evidence-pdf", generatePdf, GeneratePdfResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void generatePdf_invalid_input() {
    var generatePdf = new GeneratePdfRequest();
    generatePdf.setClaimSubmissionId("1234");
    generatePdf.setVeteranInfo(new VeteranInfo());
    generatePdf.setEvidence(new AbdEvidence());
    var response = post("/v1/evidence-pdf", generatePdf, Object.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
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

    var fetchPdfResponse = new FetchPdfResponse("1234", "ERROR", null);

    mockFetchPdfEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.fromFunction(
            (Function<Object, Object>) o -> toJsonString(fetchPdfResponse)));

    var response = get("/v1/evidence-pdf/1234", null, String.class);
    assertNotNull(response);
  }

  @SneakyThrows
  private String toJsonString(Object o) {
    return objectMapper.writeValueAsString(o);
  }

  private <I, O> ResponseEntity<O> post(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.POST, responseType);
  }

  private <I, O> ResponseEntity<O> get(String url, I request, Class<O> responseType) {
    return exchange(url, request, HttpMethod.GET, responseType);
  }

  private <I, O> ResponseEntity<O> exchange(
      String url, I request, HttpMethod method, Class<O> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "ec4624eb-a02d-4d20-bac6-095b98a792a2");
    var httpEntity = new HttpEntity<>(request, headers);
    return testRestTemplate.exchange(url, method, httpEntity, responseType);
  }

  @SneakyThrows
  private String claimToResponse(Claim claim, boolean evidence) {
    var response = new HealthDataAssessmentResponse();
    response.setDiagnosticCode(claim.getDiagnosticCode());
    response.setVeteranIcn(claim.getVeteranIcn());
    response.setErrorMessage("I am not a real endpoint.");
    if (evidence) {
      response.setEvidence(new AbdEvidence());
    }
    return objectMapper.writeValueAsString(response);
  }
}
