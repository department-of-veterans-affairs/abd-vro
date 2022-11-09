package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.*;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.mas.service.MasTransferObject;
import gov.va.vro.service.spi.audit.AuditEventService;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasIntegrationRoutes extends RouteBuilder {

  public static final String ENDPOINT_MAS =
      "rabbitmq:mas-notification-exchange?queue=mas-notification-queue&routingKey=mas-notification&requestTimeout=0";

  public static final String ENDPOINT_AUTOMATED_CLAIM = "direct:automated-claim";

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  private final MasPollingProcessor masPollingProcessor;

  private final AuditEventService auditEventService;

  private final MasCollectionService masCollectionService;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Override
  public void configure() {
    configureAuditing();
    configureAutomatedClaim();
    configureMasProcessing();
    configureOrderExamStatus();
  }

  private void configureAutomatedClaim() {
    String routeId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(routeId)
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .to(ENDPOINT_MAS);

    from(ENDPOINT_MAS)
        .routeId("mas-claim-processing")
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .process(masPollingProcessor)
        .setExchangePattern(ExchangePattern.InOnly)
        .log("MAS response: ${body}");
  }

  private void configureMasProcessing() {
    String routeId = "mas-processing";
    String lighthouseEndpoint = "direct:lighthouse-claim-submit";
    String collectEvidenceEndpoint = "direct:collect-evidence";
    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .setProperty("claim", simple("${body}"))
        .to(collectEvidenceEndpoint) // collect evidence from lighthouse and MAS
        .setProperty("evidence", simple("${body}"))
        .routingSlip(
            method(
                slipClaimSubmitRouter, "routeHealthAssessV2")) // TODO: call "health assess" service
        .unmarshal(new JacksonDataFormat(AbdEvidenceWithSummary.class))
        .process( // TODO: This is to print all the validation errors
            new Processor() {
              @Override
              public void process(Exchange exchange) {
                MasAutomatedClaimPayload claimPayload =
                    (MasAutomatedClaimPayload) exchange.getProperty("claim");
                AbdEvidenceWithSummary evidence =
                    exchange.getMessage().getBody(AbdEvidenceWithSummary.class);
                HealthDataAssessment assessment =
                    (HealthDataAssessment) exchange.getProperty("evidence");
                if (evidence.getErrorMessage() != null) {
                  log.error("Health Assessment Failed");
                }
                // FAKE IT: Let's pretend Health assessment passed
                var masTransferObject =
                    new MasTransferObject(claimPayload, assessment.getEvidence());
                exchange.getMessage().setBody(masTransferObject);
              }
            })
        //
        // TODO: call pcOrderExam in the absence of evidence
        // TODO: Call claim status update
        .process(FunctionProcessor.fromFunction(MasCollectionService::getGeneratePdfPayload))
        .to(PrimaryRoutes.ENDPOINT_GENERATE_PDF);
    // TODO upload PDF

    from(collectEvidenceEndpoint)
        .routeId("mas-automated-claim-collect-evidence")
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
        .routeId("mas-automated-claim-lighthouse")
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
    // This route does not do anything, but an audit event is persisted
    String routeId = "mas-exam-order-status";
    from(ENDPOINT_EXAM_ORDER_STATUS).routeId(routeId).log("Invoked " + routeId);
  }

  private void configureAuditing() {
    String transform_uri = "seda:audit-transform?multipleConsumers=true";
    onException(Throwable.class)
        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
        .setProperty("originalRouteId", simple("${exchange.routeId}"))
        .setProperty("recipientList", simple("seda:audit-event"))
        .to(transform_uri);

    interceptFrom("*")
        .filter(exchange -> exchange.getFromRouteId().startsWith("mas-"))
        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
        .setProperty("originalRouteId", simple("${exchange.fromRouteId}"))
        .setProperty("recipientList", constant("seda:audit-event"))
        .to(transform_uri);

    from(transform_uri)
        .process(new ExchangeAuditTransformer())
        .recipientList(exchangeProperty("recipientList"));

    from("seda:audit-event")
        .process(
            exchange -> {
              AuditEvent event = exchange.getMessage().getBody(AuditEvent.class);
              auditEventService.logEvent(event);
            });
  }
}
