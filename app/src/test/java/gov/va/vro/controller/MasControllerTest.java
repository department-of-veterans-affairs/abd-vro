package gov.va.vro.controller;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.BipServiceTestConfiguration;
import gov.va.vro.MasTestData;
import gov.va.vro.api.rrd.responses.MasResponse;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.repository.AuditEventRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BipServiceTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MasControllerTest extends BaseControllerTest {

  @EndpointInject("mock:vro-notify")
  private MockEndpoint mockMasOfframpEndpoint;

  @EndpointInject("mock:mas-complete")
  private MockEndpoint mockMasCompleteEndpoint;

  @Autowired private CamelContext camelContext;

  @Autowired private ClaimRepository claimRepository;

  @Autowired private ClaimSubmissionRepository claimSubmissionRepository;

  @Autowired private AuditEventRepository auditEventRepository;

  public static final String DEFAULT_ID_TYPE = "va.gov-Form526Submission";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void testJsonConversion() {
    ObjectMapper objectMapper = new ObjectMapper();
    var request =
        objectMapper.readValue(
            new ClassPathResource("mas-request.json").getFile(), MasAutomatedClaimRequest.class);
    assertEquals("Rick", request.getFirstName());
    assertEquals("Smith", request.getLastName());
    assertEquals(123, request.getCollectionId());
    assertEquals("2002-12-12", request.getDateOfBirth());
    assertEquals("X", request.getVeteranIdentifiers().getEdipn());
    assertEquals("X", request.getVeteranIdentifiers().getVeteranFileId());
    assertEquals("X", request.getVeteranIdentifiers().getParticipantId());
    assertEquals("X", request.getVeteranIdentifiers().getIcn());
    assertEquals("X", request.getVeteranIdentifiers().getSsn());
    assertEquals("1233", request.getClaimDetail().getConditions().getDiagnosticCode());
    assertEquals("VA.GOV", request.getClaimDetail().getClaimSubmissionSource());
  }

  @Test
  void automatedClaimInvalidRequest() {
    MasAutomatedClaimPayload request =
        MasAutomatedClaimPayload.builder().dateOfBirth("2002-12-12").collectionId(123).build();
    var responseEntity = post("/v2/automatedClaim", request, MasResponse.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  void automatedClaimOutOfScope() throws Exception {
    var offrampCalled = new AtomicBoolean(false);
    adviceWith(
            camelContext,
            "vro-notify",
            route ->
                route
                    .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT)
                    .skipSendToOriginalEndpoint()
                    .to(mockMasOfframpEndpoint))
        .end();
    // The mock endpoint returns a valid response
    mockMasOfframpEndpoint.whenAnyExchangeReceived(
        exchange -> {
          AuditEvent auditEvent = exchange.getMessage().getBody(AuditEvent.class);
          assertEquals(
              "Claim with collection id: 123, diagnostic code: 1233, and"
                  + " disability action type: INCREASE is not in scope.",
              auditEvent.getMessage());
          offrampCalled.set(true);
        });
    var completeCalled = new AtomicBoolean(false);
    adviceWith(
            camelContext,
            "mas-complete-claim",
            route ->
                route
                    .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_MAS_COMPLETE)
                    .skipSendToOriginalEndpoint()
                    .to(mockMasCompleteEndpoint))
        .end();
    mockMasCompleteEndpoint.whenAnyExchangeReceived(
        exchange -> {
          MasProcessingObject mpo = exchange.getMessage().getBody(MasProcessingObject.class);
          assertNotNull(mpo.getClaimPayload());
          completeCalled.set(true);
        });

    var request = MasTestData.getMasAutomatedClaimRequest();
    var responseEntity = post("/v2/automatedClaim", request, MasResponse.class);
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    assertFalse(offrampCalled.get());
    assertFalse(completeCalled.get());
    verifyClaimPersisted(request);
  }

  @Test
  void automatedClaimInScope() throws Exception {

    adviceWith(
            camelContext,
            "mas-claim-notification",
            route ->
                route
                    .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_AUTOMATED_CLAIM)
                    .skipSendToOriginalEndpoint()
                    .to("mock:mas-notification"))
        .end();
    // The mock endpoint returns a valid response
    mockMasOfframpEndpoint.whenAnyExchangeReceived(exchange -> {});

    var request = MasTestData.getMasAutomatedClaimRequest(567, "7101", "999");
    var responseEntity = post("/v2/automatedClaim", request, MasResponse.class);
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    mockMasOfframpEndpoint.expectedMessageCount(1);

    verifyClaimPersisted(request);
  }

  @Test
  void orderExamStatus() {
    var payload =
        MasExamOrderStatusPayload.builder().collectionId(123).collectionStatus("UNKNOWN").build();
    ResponseEntity<MasResponse> response =
        post("/v2/examOrderingStatus", payload, MasResponse.class);
    assertEquals(
        "Received Exam Order Status for collection Id 123.", response.getBody().getMessage());
  }

  @Test
  void orderExamStatusMissingCollectionId() {
    var payload = MasExamOrderStatusPayload.builder().collectionStatus("UNKNOWN").build();
    ResponseEntity<MasResponse> response =
        post("/v2/examOrderingStatus", payload, MasResponse.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  private void verifyClaimPersisted(MasAutomatedClaimRequest request) {
    var claim = claimRepository.findByVbmsId(request.getClaimDetail().getBenefitClaimId()).get();
    var claimSubmissionList =
        claimSubmissionRepository.findByReferenceIdAndIdType(
            String.valueOf(request.getCollectionId()), DEFAULT_ID_TYPE);
    for (ClaimSubmissionEntity submission : claimSubmissionList) {
      assertEquals(request.getCollectionId().toString(), submission.getReferenceId());
    }
    assertEquals(request.getVeteranIdentifiers().getIcn(), claim.getVeteran().getIcn());
    var contentions = claim.getContentions();
    assertEquals(1, contentions.size());
    var contention = contentions.get(0);
    assertEquals(
        request.getClaimDetail().getConditions().getDiagnosticCode(),
        contention.getDiagnosticCode());
    claimRepository.delete(claim);
  }
}
