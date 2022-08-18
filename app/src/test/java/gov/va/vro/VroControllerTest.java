package gov.va.vro;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.model.AbdEvidence;
import gov.va.vro.api.model.VeteranInfo;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FetchPdfResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.provider.camel.FunctionProcessor;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.provider.camel.SlipClaimSubmitRouter;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @MockBean private SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Autowired @InjectMocks private PrimaryRoutes primaryRoutes;

  @Autowired private ClaimRepository claimRepository;

  @Autowired protected CamelContext camelContext;

  @EndpointInject("mock:generate-pdf")
  private MockEndpoint mockGeneratePdfEndpoint;

  @EndpointInject("mock:fetch-pdf")
  private MockEndpoint mockFetchPdfEndpoint;

  @Test
  void postHealthAssessment() {

    Mockito.when(slipClaimSubmitRouter.routeClaimSubmit(Mockito.any(), Mockito.anyMap()))
        .thenReturn("direct:hello");

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
    System.out.println(response);
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
}
