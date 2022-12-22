package gov.va.vro.controller;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.MasTestData;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MasControllerTest extends BaseControllerTest {

  @EndpointInject("mock:mas-notification")
  private MockEndpoint mockMasNotificationEndpoint;

  @EndpointInject("mock:mas-offramp")
  private MockEndpoint mockMasOffRampEndpoint;

  @Autowired private CamelContext camelContext;

  @Autowired @SpyBean private AuditEventService auditEventService;

  @Test
  @SneakyThrows
  void testJsonConversion() {
    ObjectMapper objectMapper = new ObjectMapper();
    var request =
        objectMapper.readValue(
            new ClassPathResource("mas-request.json").getFile(), MasAutomatedClaimPayload.class);
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
    adviceWith(
            camelContext,
            "mas-slack-event",
            route ->
                route
                    .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_SLACK_EVENT)
                    .skipSendToOriginalEndpoint()
                    .to("mock:mas-notification"))
        .end();
    // The mock endpoint returns a valid response
    mockMasNotificationEndpoint.whenAnyExchangeReceived(exchange -> {});

    adviceWith(
            camelContext,
            "mas-complete-claim",
            route ->
                route
                    .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_MAS_COMPLETE)
                    .skipSendToOriginalEndpoint()
                    .to("mock:mas-offramp"))
        .end();
    mockMasOffRampEndpoint.whenAnyExchangeReceived(exchange -> {});

    MasAutomatedClaimPayload request = MasTestData.getMasAutomatedClaimPayload();
    var responseEntity = post("/v2/automatedClaim", request, MasResponse.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    mockMasNotificationEndpoint.assertIsSatisfied();
    mockMasOffRampEndpoint.assertIsSatisfied();
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
    mockMasNotificationEndpoint.whenAnyExchangeReceived(exchange -> {});

    MasAutomatedClaimPayload request = MasTestData.getMasAutomatedClaimPayload(567, "7101", "999");
    var responseEntity = post("/v2/automatedClaim", request, MasResponse.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    mockMasNotificationEndpoint.expectedMessageCount(1);
  }

  @Test
  void orderExamStatus() {
    ArgumentCaptor<AuditEvent> auditEventArgumentCaptor = ArgumentCaptor.forClass(AuditEvent.class);
    var payload =
        MasExamOrderStatusPayload.builder().collectionId(123).collectionStatus("UNKNOWN").build();
    ResponseEntity<MasResponse> response =
        post("/v2/examOrderingStatus", payload, MasResponse.class);
    assertEquals("123", response.getBody().getId());
    assertEquals("Received", response.getBody().getMessage());
    // TODO: verify event logged
    //    Mockito.verify(auditEventService).logEvent(auditEventArgumentCaptor.capture());
    //    var event = auditEventArgumentCaptor.getValue();
    //    assertEquals("123", event.getEventId());
    //    assertEquals("mas-exam-order-status", event.getRouteId());
  }
}
