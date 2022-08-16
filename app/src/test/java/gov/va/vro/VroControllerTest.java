package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.provider.camel.SlipClaimSubmitRouter;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VroControllerTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @MockBean private SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Autowired @InjectMocks private PrimaryRoutes primaryRoutes;

  @Autowired private ClaimRepository claimRepository;

  @Test
  void postHealthAssessment() {

    Mockito.when(slipClaimSubmitRouter.routeClaimSubmit(Mockito.any(), Mockito.anyMap()))
        .thenReturn("direct:hello");

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    var responseEntity1 = post("/v1/health-data-assessment", request);

    assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
    HealthDataAssessmentResponse response1 = responseEntity1.getBody();
    assertEquals(request.getDiagnosticCode(), response1.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response1.getVeteranIcn());

    // Now submit an existing claim:
    var responseEntity2 = post("/v1/health-data-assessment", request);
    assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
    HealthDataAssessmentResponse response2 = responseEntity2.getBody();
    assertEquals(request.getDiagnosticCode(), response2.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response2.getVeteranIcn());

    // side effect: claim is created in the DB
    Optional<ClaimEntity> claimEntityOptional =
        claimRepository.findByClaimSubmissionIdAndIdType("1234", "va.gov-Form526Submission");
    assertTrue(claimEntityOptional.isPresent());
  }

  private ResponseEntity<HealthDataAssessmentResponse> post(
      String url, HealthDataAssessmentRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "ec4624eb-a02d-4d20-bac6-095b98a792a2");
    var httpEntity = new HttpEntity<>(request, headers);
    return testRestTemplate.exchange(
        url, HttpMethod.POST, httpEntity, HealthDataAssessmentResponse.class);
  }
}
