package gov.va.vro.routes;

import static gov.va.vro.service.provider.camel.PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import gov.va.vro.model.mas.request.MasOrderExamRequest;
import gov.va.vro.persistence.repository.AuditEventRepository;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.IMasApiService;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

public class MasIntegrationRoutesTest extends BaseIntegrationTest {

  private static final long DEFAULT_REQUEST_TIMEOUT = 120000;

  @Autowired CamelEntrance camelEntrance;

  @MockBean IMasApiService masApiService;

  @Autowired @InjectMocks MasCollectionService masCollectionService;

  @Autowired CamelContext camelContext;

  @Autowired AuditEventRepository auditEventRepository;

  @EndpointInject("mock:sufficiency-assess")
  private MockEndpoint mockSufficiencyAssess;

  @EndpointInject("mock:claim-submit-full")
  private MockEndpoint mockClaimSubmit;

  @EndpointInject("mock:empty-endpoint")
  private MockEndpoint mockEmptyEndpoint;

  @Test
  void processClaimSufficientEvidence() throws Exception {
    var mpo = processClaim(true);
    Thread.sleep(200);
    var audits = auditEventRepository.findByEventIdOrderByEventTimeAsc(mpo.getEventId());
    assertTrue(
        audits.stream()
            .filter(audit -> audit.getMessage().startsWith("Sufficient evidence"))
            .findFirst()
            .isPresent());
  }

  @Test
  void processClaimInsufficientEvidence() throws Exception {
    var mpo = processClaim(false);
    Thread.sleep(200);
    var audits = auditEventRepository.findByEventIdOrderByEventTimeAsc(mpo.getEventId());
    assertTrue(
        audits.stream()
            .filter(audit -> audit.getMessage().startsWith("There is insufficient evidence"))
            .findFirst()
            .isPresent());
  }

  @Test
  void processClaimInsufficientEvidenceAccessError() throws Exception {
    var mpo = processClaim(null);
    Thread.sleep(200);
    var audits = auditEventRepository.findByEventIdOrderByEventTimeAsc(mpo.getEventId());
    assertTrue(
        audits.stream()
            .filter(audit -> audit.getMessage().startsWith("Sufficiency cannot be determined"))
            .findFirst()
            .isPresent());
  }

  private MasProcessingObject processClaim(Boolean sufficientEvidence) throws Exception {

    // Mock a return value when claim-submit-full (lighthouse) is invoked
    replaceEndpoint(
        "claim-submit-full",
        "rabbitmq://claim-submit-exchange?queue=claim-submit&"
            + "requestTimeout="
            + DEFAULT_REQUEST_TIMEOUT
            + "&routingKey=code.hypertension",
        "mock:claim-submit-full");

    mockClaimSubmit.whenAnyExchangeReceived(
        exchange -> {
          var assessment = new HealthDataAssessment();
          exchange.getMessage().setBody(new ObjectMapper().writeValueAsBytes(assessment));
        });

    // Mock a return value when health assess is invoked

    replaceEndpoint(
        "mas-processing",
        "rabbitmq:health-assess-exchange?routingKey=health-sufficiency-assess.hypertension&"
            + "requestTimeout="
            + DEFAULT_REQUEST_TIMEOUT,
        "mock:sufficiency-assess");

    mockSufficiencyAssess.whenAnyExchangeReceived(
        exchange -> {
          var evidence = new AbdEvidenceWithSummary();
          evidence.setEvidence(new AbdEvidence());
          evidence.setSufficientForFastTracking(sufficientEvidence);
          exchange.getMessage().setBody(new ObjectMapper().writeValueAsBytes(evidence));
        });

    // Mock NOOP when generate PDF endpoints are called

    replaceEndpoint(
        "generate-pdf",
        RabbitMqCamelUtils.wiretapProducer(INCOMING_CLAIM_WIRETAP),
        "mock:empty-endpoint");

    replaceEndpoint(
        "generate-pdf",
        "rabbitmq://pdf-generator?queue=generate-pdf&routingKey=generate-pdf",
        "mock:empty-endpoint");

    replaceEndpoint(
        "mas-processing", MasIntegrationRoutes.ENDPOINT_UPLOAD_PDF, "mock:empty-endpoint");

    replaceEndpoint("mas-rfd", MasIntegrationRoutes.ENDPOINT_UPLOAD_PDF, "mock:empty-endpoint");

    replaceEndpoint(
        "mas-order-exam", MasIntegrationRoutes.ENDPOINT_UPLOAD_PDF, "mock:empty-endpoint");

    mockEmptyEndpoint.whenAnyExchangeReceived(exchange -> {});

    // set up a Mas Request and invoke processClaim
    int collectionId = 123;
    var collectionAnnotation = new MasCollectionAnnotation();
    MasDocument document = MasTestData.createHypertensionDocument();
    collectionAnnotation.setCollectionsId(collectionId);
    collectionAnnotation.setDocuments(Collections.singletonList(document));
    Mockito.when(masApiService.getCollectionAnnotations(collectionId))
        .thenReturn(Collections.singletonList(collectionAnnotation));
    var payload = MasTestData.getMasAutomatedClaimPayload();
    var response = camelEntrance.processClaim(payload);

    // verify if order exam was called based on the sufficient evidence flag
    if (sufficientEvidence == null) {
      Mockito.verify(masApiService, Mockito.never()).orderExam(Mockito.any());
    } else if (sufficientEvidence != null && sufficientEvidence == true) {
      Mockito.verify(masApiService, Mockito.never()).orderExam(Mockito.any());
    } else if (sufficientEvidence != null && sufficientEvidence == false) {
      var argumentCaptor = ArgumentCaptor.forClass(MasOrderExamRequest.class);
      Mockito.verify(masApiService, Mockito.times(1)).orderExam(argumentCaptor.capture());
      MasOrderExamRequest orderExamRequest = argumentCaptor.getValue();
      assertEquals(collectionId, orderExamRequest.getCollectionsId());
    }
    return response;
  }

  private void replaceEndpoint(String routeId, String fromUri, String toUri) throws Exception {
    adviceWith(
        camelContext,
        routeId,
        // TODO: Consider using `weaveById().replace()` for rabbitmq endpoints to avoid "Failed to
        // create connection."
        // https://tomd.xyz/mock-endpoints-are-real: "Original endpoints are still initialised, even
        // if they have been mocked."
        route -> route.interceptSendToEndpoint(fromUri).skipSendToOriginalEndpoint().to(toUri));
  }
}
