package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.provider.camel.SlipClaimSubmitRouter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VroControllerTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @MockBean private SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Autowired @InjectMocks private PrimaryRoutes primaryRoutes;

  @Test
  void postHealthAssessment() {

    Mockito.when(slipClaimSubmitRouter.routeClaimSubmit(Mockito.any(), Mockito.anyMap()))
        .thenReturn("direct:hello");

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    ResponseEntity<HealthDataAssessmentResponse> responseEntity1 =
        testRestTemplate.postForEntity(
            "/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);
    assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
    HealthDataAssessmentResponse response1 = responseEntity1.getBody();
    assertEquals(request.getDiagnosticCode(), response1.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response1.getVeteranIcn());

    // Now submit an existing claim:
    ResponseEntity<HealthDataAssessmentResponse> responseEntity2 =
        testRestTemplate.postForEntity(
            "/v1/health-data-assessment", request, HealthDataAssessmentResponse.class);
    assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
    HealthDataAssessmentResponse response2 = responseEntity2.getBody();
    assertEquals(request.getDiagnosticCode(), response2.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response2.getVeteranIcn());
  }
}
