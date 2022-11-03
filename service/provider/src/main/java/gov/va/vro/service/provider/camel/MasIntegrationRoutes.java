package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.event.JsonConverter;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.event.AuditEventProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MasIntegrationRoutes extends RouteBuilder {

  public static final String ENDPOINT_MAS =
      "rabbitmq:mas-notification-exchange?queue=mas-notification-queue&routingKey=mas-notification&requestTimeout=0";

  public static final String ENDPOINT_AUTOMATED_CLAIM = "direct:automated-claim";

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  private final MasPollingProcessor masPollingProcessor;

  private final AuditEventProcessor auditEventProcessor;

  private final MasCollectionService masCollectionService;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Override
  public void configure() {
    configureExceptionHandling();
    configureAutomatedClaim();
    configureOrderExamStatus();
    configureMasProcessing();
  }

  private void configureAutomatedClaim() {
    String routeId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(routeId)
        .process(
            auditEventProcessor.event(
                routeId, "Setting a delay before staring Automated claim processing."))
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .process(auditEventProcessor.event(routeId, "Calling endpoint " + ENDPOINT_MAS))
        .to(ENDPOINT_MAS);

    from(ENDPOINT_MAS)
        .routeId("mas-claim-processing")
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .process(
            auditEventProcessor.event("mas-claim-processing", "Entering endpoint " + ENDPOINT_MAS))
        .process(masPollingProcessor)
        .setExchangePattern(ExchangePattern.InOnly)
        .log("MAS response: ${body}");
  }

  // TODO: add back event capture
  private void configureMasProcessing() {
    String routeId = "mas-processing";
    String lighthouseEndpoint = "direct:lighthouse-claim-submit";
    String collectEvidenceEndpoint = "direct:collect-evidence";
    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .to(collectEvidenceEndpoint); // collect evidence from lighthouse and MAS
    // TODO: call "health assess" service based on condition
    // .routingSlip(method(slipClaimSubmitRouter, "routeHealthAssess"));
    // TODO: call pcOrderExam in the absence of evidence
    // TODO: Call claim status update
    // TODO .process(FunctionProcessor.fromFunction(MasCollectionService::getGeneratePdfPayload))
    // TODO:  .to(PrimaryRoutes.ENDPOINT_GENERATE_PDF);
    // TODO upload PDF

    from(collectEvidenceEndpoint)
        .routeId("automated-claim-collect-evidence")
        .multicast(new GroupedBodyAggregationStrategy())
        .process(
            FunctionProcessor.fromFunction(masCollectionService::collectAnnotations)) // call MAS
        .to(lighthouseEndpoint) // call Lighthouse
        .end() // end multicast
        .process( // combine evidence
            FunctionProcessor.fromFunction(
                (Function<List<HealthDataAssessment>, HealthDataAssessment>)
                    abdEvidences ->
                        MasCollectionService.combineEvidence(
                            abdEvidences.get(0), abdEvidences.get(1))));

    from(lighthouseEndpoint)
        .routeId("automated-claim-lighthouse")
        .process(
            FunctionProcessor.fromFunction(
                (Function<MasAutomatedClaimPayload, Claim>)
                    payload ->
                        Claim.builder()
                            .claimSubmissionId(payload.getClaimDetail().getBenefitClaimId())
                            .diagnosticCode(
                                payload.getClaimDetail().getConditions().getDiagnosticCode())
                            .veteranIcn(payload.getVeteranIdentifiers().getIcn())
                            .build()))
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .unmarshal(new JacksonDataFormat(HealthDataAssessment.class));
  }

  private void configureOrderExamStatus() {
    String routeId = "exam-order-status";
    from(ENDPOINT_EXAM_ORDER_STATUS)
        .routeId(routeId)
        .process(
            auditEventProcessor.event(
                routeId, "Entering endpoint " + ENDPOINT_EXAM_ORDER_STATUS, new JsonConverter()));
  }

  private void configureExceptionHandling() {
    onException(Throwable.class)
        .process(
            exchange -> {
              Throwable exception = (Throwable) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
              var message = exchange.getMessage();
              var body = message.getBody();
              auditEventProcessor.logException(body, exception, exchange.getFromRouteId());
            });
  }
}
