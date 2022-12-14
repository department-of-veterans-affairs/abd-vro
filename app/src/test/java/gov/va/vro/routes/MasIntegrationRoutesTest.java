package gov.va.vro.routes;

import static gov.va.vro.service.provider.camel.PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import gov.va.vro.model.mas.MasOrderExamReq;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.camel.VroCamelUtils;
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

  @Autowired CamelEntrance camelEntrance;

  @MockBean IMasApiService masApiService;

  @Autowired @InjectMocks MasCollectionService masCollectionService;

  @Autowired CamelContext camelContext;

  @EndpointInject("mock:sufficiency-assess")
  private MockEndpoint mockSufficiencyAssess;

  @EndpointInject("mock:claim-submit")
  private MockEndpoint mockClaimSubmit;

  @EndpointInject("mock:empty-endpoint")
  private MockEndpoint mockEmptyEndpoint;

  @Test
  void processClaimSufficientEvidence() throws Exception {
    processClaim(true);
  }

  @Test
  void processClaimInsufficientEvidence() throws Exception {
    processClaim(false);
  }

  private void processClaim(boolean sufficientEvidence) throws Exception {

    // Mock a return value when claim-submit (lighthouse) is invoked
    replaceEndpoint(
        "claim-submit",
        "rabbitmq://claim-submit-exchange?queue=claim-submit&"
            + "requestTimeout=60000&routingKey=code.1233",
        "mock:claim-submit");

    mockClaimSubmit.whenAnyExchangeReceived(
        exchange -> {
          var assessment = new HealthDataAssessment();
          exchange.getMessage().setBody(new ObjectMapper().writeValueAsBytes(assessment));
        });

    // Mock a return value when health assess is invoked

    replaceEndpoint(
        "mas-processing",
        "rabbitmq:health-assess-exchange?routingKey=health-sufficiency-assess.1233&"
            + "requestTimeout=60000",
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
        VroCamelUtils.wiretapProducer(INCOMING_CLAIM_WIRETAP),
        "mock:empty-endpoint");

    replaceEndpoint(
        "generate-pdf",
        "rabbitmq://pdf-generator?queue=generate-pdf&routingKey=generate-pdf",
        "mock:empty-endpoint");

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
    camelEntrance.processClaim(payload);

    // verify if order exam was called based on the sufficient evidence flag
    if (sufficientEvidence) {
      Mockito.verify(masApiService, Mockito.never()).orderExam(Mockito.any());
    } else {
      var argumentCaptor = ArgumentCaptor.forClass(MasOrderExamReq.class);
      Mockito.verify(masApiService, Mockito.times(1)).orderExam(argumentCaptor.capture());
      MasOrderExamReq orderExamRequest = argumentCaptor.getValue();
      assertEquals(collectionId, orderExamRequest.getCollectionsId());
    }
  }

  private void replaceEndpoint(String routeId, String fromUri, String toUri) throws Exception {
    adviceWith(
        camelContext,
        routeId,
        route -> route.interceptSendToEndpoint(fromUri).skipSendToOriginalEndpoint().to(toUri));
  }
}
