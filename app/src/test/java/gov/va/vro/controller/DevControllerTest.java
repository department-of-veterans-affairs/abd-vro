package gov.va.vro.controller;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.config.AppTestUtil;
import gov.va.vro.controller.exception.ClaimProcessingError;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.service.spi.model.Claim;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@CamelSpringBootTest
class DevControllerTest extends BaseControllerTest {

  @Autowired private AppTestUtil util;

  @Autowired protected CamelContext camelContext;

  @EndpointInject("mock:claim-submit")
  private MockEndpoint mockSubmitClaimEndpoint;

  @Test
  @DirtiesContext
  void postHealthAssessment() throws Exception {

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
                        + "&routingKey=code.1701&requestTimeout=60000")
                .skipSendToOriginalEndpoint()
                .to("mock:claim-submit"));
    // The mock endpoint returns a valid response
    mockSubmitClaimEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<Claim, String>fromFunction(claim -> util.claimToResponse(claim, true)));

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    var responseEntity1 = post("/v1/health-data-assessment", request, HealthDataAssessment.class);

    assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
    HealthDataAssessment response1 = responseEntity1.getBody();
    assertNotNull(response1);
    assertEquals(request.getDiagnosticCode(), response1.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response1.getVeteranIcn());

    // Now submit an existing claim:
    var responseEntity2 = post("/v1/health-data-assessment", request, HealthDataAssessment.class);
    assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
    HealthDataAssessment response2 = responseEntity2.getBody();
    assertNotNull(response2);
    assertEquals(request.getDiagnosticCode(), response2.getDiagnosticCode());
    assertEquals(request.getVeteranIcn(), response2.getVeteranIcn());

    // side effect: claim is created in the DB
    Optional<ClaimEntity> claimEntityOptional =
        claimRepository.findByClaimSubmissionIdAndIdType("1234", "va.gov-Form526Submission");
    assertTrue(claimEntityOptional.isPresent());
  }

  @Test
  @DirtiesContext
  void claimSubmit_missing_evidence() throws Exception {
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
        FunctionProcessor.<Claim, String>fromFunction(claim -> util.claimToResponse(claim, false)));

    HealthDataAssessmentRequest request = new HealthDataAssessmentRequest();
    request.setClaimSubmissionId("1234");
    request.setVeteranIcn("icn");
    request.setDiagnosticCode("1701");

    var responseEntity = post("/v1/health-data-assessment", request, ClaimProcessingError.class);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    var claimProcessingError = responseEntity.getBody();
    assertNotNull(claimProcessingError);
    assertEquals("No evidence found.", claimProcessingError.getMessage());
    assertEquals("1234", claimProcessingError.getClaimSubmissionId());
  }
}
