package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.*;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasOrderExamProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.services.HealthEvidenceProcessor;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasIntegrationRoutes extends RouteBuilder {

  public static final String ENDPOINT_MAS =
      "rabbitmq:mas-notification-exchange?queue=mas-notification-queue&routingKey=mas-notification&requestTimeout=0";

  public static final String ENDPOINT_AUTOMATED_CLAIM = "seda:automated-claim";

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  public static final String ENDPOINT_AUDIT_EVENT = "seda:audit-event";

  public static final String ENDPOINT_SLACK_EVENT = "seda:slack-event";

  public static final String ENDPOINT_MAS_COMPLETE = "direct:mas-complete";

  private final BipClaimService bipClaimService;

  private final AuditEventService auditEventService;

  private final MasConfig masConfig;

  private final MasPollingProcessor masPollingProcessor;

  private final MasOrderExamProcessor masOrderExamProcessor;

  private final MasCollectionService masCollectionService;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Override
  public void configure() {
    configureAuditing();
    configureAutomatedClaim();
    configureMasProcessing();
    configureOrderExamStatus();
    configureCompleteProcessing();
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
        .setExchangePattern(ExchangePattern.InOnly);
  }

  private void configureMasProcessing() {
    String routeId = "mas-processing";
    String lighthouseEndpoint = "direct:lighthouse-claim-submit";
    String collectEvidenceEndpoint = "direct:collect-evidence";
    String orderExamEndpoint = "direct:order-exam";
    String uploadPdfEndpoint = "direct:upload-pdf";
    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .setProperty("claim", simple("${body}"))
        .to(collectEvidenceEndpoint) // collect evidence from lighthouse and MAS
        .routingSlip(method(slipClaimSubmitRouter, "routeHealthSufficiency"))
        .unmarshal(new JacksonDataFormat(AbdEvidenceWithSummary.class))
        .process(new HealthEvidenceProcessor())
        .process(MasIntegrationProcessors.generatePdfProcessor())
        .to(PrimaryRoutes.ENDPOINT_GENERATE_PDF)
        .process(
            exchange -> {
              MasAutomatedClaimPayload claimPayload =
                  (MasAutomatedClaimPayload) exchange.getProperty("claim");
              exchange.getMessage().setBody(claimPayload);
            })
        .to(orderExamEndpoint)
        .to(uploadPdfEndpoint)
        .to(ENDPOINT_MAS_COMPLETE);

    from(collectEvidenceEndpoint)
        .routeId("mas-automated-claim-collect-evidence")
        .multicast(new GroupedExchangeAggregationStrategy())
        .process(
            FunctionProcessor.fromFunction(masCollectionService::collectAnnotations)) // call MAS
        .to(lighthouseEndpoint) // call Lighthouse
        .end() // end multicast
        .process(MasIntegrationProcessors.combineExchangesProcessor());

    from(lighthouseEndpoint)
        .routeId("mas-automated-claim-lighthouse")
        .process(MasIntegrationProcessors.payloadToClaimProcessor())
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .unmarshal(new JacksonDataFormat(HealthDataAssessment.class));

    // Call "Order Exam" in the absence of evidence
    from(orderExamEndpoint)
        .routeId("mas-order-exam")
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking} == false"))
        .process(masOrderExamProcessor)
        .setExchangePattern(ExchangePattern.InOnly)
        .log("MAS Order Exam response: ${body}")
        .end();

    from(uploadPdfEndpoint).routeId("mas-upload-pdf").log("TODO: upload PDF");
  }

  private void configureOrderExamStatus() {
    // This route does not do anything, but an audit event is persisted
    String routeId = "mas-exam-order-status";
    from(ENDPOINT_EXAM_ORDER_STATUS).routeId(routeId).log("Invoked " + routeId);
  }

  private void configureCompleteProcessing() {
    from(ENDPOINT_MAS_COMPLETE)
        .routeId("mas-complete-claim")
        .log(" >> Request to complete claim received.")
        .bean(FunctionProcessor.fromFunction(bipClaimService::removeSpecialIssue))
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking}"))
        .bean(FunctionProcessor.fromFunction(bipClaimService::markAsRFD))
        .end()
        .bean(bipClaimService, "completeProcessing");
  }

  public void configureAuditing() {
    String transform_uri = "seda:audit-transform?multipleConsumers=true";

    // Capture exceptions
    onException(Throwable.class)
        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
        .setProperty("originalRouteId", simple("${exchange.fromRouteId}"))
        .setProperty("recipientList", constant(ENDPOINT_AUDIT_EVENT, ENDPOINT_SLACK_EVENT))
        .to(transform_uri);

    // intercept all MAS routes
    interceptFrom("*")
        .filter(exchange -> exchange.getFromRouteId().startsWith("mas-"))
        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
        .setProperty("originalRouteId", simple("${exchange.fromRouteId}"))
        .setProperty("recipientList", constant(ENDPOINT_AUDIT_EVENT))
        .to(transform_uri);

    // Transform to an AuditEvent and send to recipients
    from(transform_uri)
        .process(new ExchangeAuditTransformer())
        .recipientList(exchangeProperty("recipientList"));

    // persist audit event
    from(ENDPOINT_AUDIT_EVENT)
        .process(
            exchange -> {
              AuditEvent event = exchange.getMessage().getBody(AuditEvent.class);
              auditEventService.logEvent(event);
            });

    from(ENDPOINT_SLACK_EVENT)
        .routeId("mas-slack-event")
        .filter(exchange -> StringUtils.isNotBlank(masConfig.getSlackExceptionWebhook()))
        .process(FunctionProcessor.fromFunction(AuditEvent::toString))
        .to(
            String.format(
                "slack:#%s?webhookUrl=%s",
                masConfig.getSlackExceptionChannel(), masConfig.getSlackExceptionWebhook()));
  }
}
