package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
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
import org.apache.camel.ExchangePattern;
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
      "rabbitmq:mas-notification-exchange?queue=mas-notification"
          + "-queue&routingKey=mas-notification&requestTimeout=0";

  public static final String ENDPOINT_AUTOMATED_CLAIM = "seda:automated-claim";

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  public static final String ENDPOINT_AUDIT_EVENT = "seda:audit-event";

  public static final String ENDPOINT_SLACK_EVENT = "seda:slack-event";

  public static final String ENDPOINT_MAS_COMPLETE = "direct:mas-complete";

  public static final String ENDPOINT_UPLOAD_PDF = "direct:upload-pdf";
  public static final String ENDPOINT_AUDIT_WIRETAP = "direct:wire";

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
    configureUploadPdf();
  }

  private void configureAutomatedClaim() {
    var checkClaimRouteId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(checkClaimRouteId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            MasIntegrationProcessors.auditProcessor(
                checkClaimRouteId, "Checking if claim is ready"))
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .to(ENDPOINT_MAS);

    var processClaimRouteId = "mas-claim-processing";
    from(ENDPOINT_MAS)
        .routeId(processClaimRouteId)
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .process(masPollingProcessor)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            MasIntegrationProcessors.auditProcessor(
                processClaimRouteId, "Staring processing claim"))
        .setExchangePattern(ExchangePattern.InOnly);
  }

  private void configureMasProcessing() {
    String routeId = "mas-processing";
    String lighthouseEndpoint = "direct:lighthouse-claim-submit";
    String collectEvidenceEndpoint = "direct:collect-evidence";
    String orderExamEndpoint = "direct:order-exam";

    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId) // input: MasAutomatedClaimPayload
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(MasIntegrationProcessors.auditProcessor(routeId, "Collecting evidence"))
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .setProperty("claim", simple("${body}"))
        .to(collectEvidenceEndpoint) // collect evidence from lighthouse and MAS
        // determine if evidence is sufficient
        .routingSlip(method(slipClaimSubmitRouter, "routeHealthSufficiency"))
        .unmarshal(new JacksonDataFormat(AbdEvidenceWithSummary.class))
        .process(new HealthEvidenceProcessor()) // returns MasTransferObject
        // Generate PDF
        .process(MasIntegrationProcessors.generatePdfProcessor())
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(MasIntegrationProcessors.auditProcessor(routeId, "Generating PDF"))
        .to(PrimaryRoutes.ENDPOINT_GENERATE_PDF)
        .setBody(simple("${exchangeProperty.claim}"))
        // Conditionally order exam
        .to(orderExamEndpoint)
        // Upload PDF
        .to(ENDPOINT_UPLOAD_PDF)
        // Check and update statuses
        .to(ENDPOINT_MAS_COMPLETE);

    from(collectEvidenceEndpoint)
        .routeId("mas-automated-claim-collect-evidence")
        .multicast(new GroupedExchangeAggregationStrategy())
        .process(
            FunctionProcessor.fromFunction(masCollectionService::collectAnnotations)) // call MAS
        .to(lighthouseEndpoint) // call Lighthouse
        .end() // end multicast
        .process(
            MasIntegrationProcessors.combineExchangesProcessor()); // returns HealthDataAssessment

    from(lighthouseEndpoint)
        .routeId("mas-automated-claim-lighthouse")
        .process(MasIntegrationProcessors.payloadToClaimProcessor())
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .unmarshal(new JacksonDataFormat(HealthDataAssessment.class));

    // Call "Order Exam" in the absence of evidence
    var orderExamRouteId = "mas-order-exam";
    from(orderExamEndpoint)
        // input: MasAutomatedClaimPayload
        .routeId(orderExamRouteId)
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking} == false"))
        .process(masOrderExamProcessor)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            MasIntegrationProcessors.auditProcessor(
                orderExamRouteId, "There is insufficient evidence. Ordering an exam"))
        .log("MAS Order Exam response: ${body}")
        .end();
  }

  private void configureUploadPdf() {
    var routeId = "mas-upload-pdf";
    from(ENDPOINT_UPLOAD_PDF)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(MasIntegrationProcessors.auditProcessor(routeId, "Uploading PDF"))
        .setBody(simple("${body.claimId}"))
        .convertBodyTo(String.class)
        .to(PrimaryRoutes.ENDPOINT_FETCH_PDF)
        .process(MasIntegrationProcessors.covertToPdfReponse())
        .process(FunctionProcessor.fromFunction(bipClaimService::uploadPdf))
        .setBody(simple("${exchangeProperty.claim}"));
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
        .bean(FunctionProcessor.fromFunction(bipClaimService::markAsRfd))
        .end()
        .bean(bipClaimService, "completeProcessing");
  }

  /** Configure auditing. */
  public void configureAuditing() {
    String transformUri = "seda:audit-transform?multipleConsumers=true";

    // Capture exceptions
    onException(Throwable.class)
        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
        .setProperty("originalRouteId", simple("${exchange.fromRouteId}"))
        .setProperty("recipientList", constant(ENDPOINT_AUDIT_EVENT, ENDPOINT_SLACK_EVENT))
        .to(transformUri);

    // intercept all MAS routes
    //    interceptFrom("*")
    //        .filter(exchange -> exchange.getFromRouteId().startsWith("mas-"))
    //        .filter(exchange -> exchange.getMessage().getBody() instanceof Auditable)
    //        .setProperty("originalRouteId", simple("${exchange.fromRouteId}"))
    //        .setProperty("recipientList", constant(ENDPOINT_AUDIT_EVENT))
    //        .to(transformUri);

    from(ENDPOINT_AUDIT_WIRETAP)
        .process(
            exchange -> {
              AuditEvent event = exchange.getMessage().getBody(AuditEvent.class);
              auditEventService.logEvent(event);
            });

    // Transform to an AuditEvent and send to recipients
    from(transformUri)
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
