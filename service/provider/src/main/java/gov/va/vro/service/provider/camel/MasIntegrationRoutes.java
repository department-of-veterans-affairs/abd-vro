package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.auditProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.combineExchangesProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.convertToMasProcessingObject;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.convertToPdfResponse;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.generatePdfProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.lighthouseContinueProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.payloadToClaimProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventPropertyProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackOffRampProcessor;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.ExternalCallException;
import gov.va.vro.service.provider.MasAccessErrProcessor;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasOrderExamProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import gov.va.vro.service.provider.services.EvidenceSummaryDocumentProcessor;
import gov.va.vro.service.provider.services.HealthAssessmentErrCheckProcessor;
import gov.va.vro.service.provider.services.HealthEvidenceProcessor;
import gov.va.vro.service.provider.services.MasAssessmentResultProcessor;
import gov.va.vro.service.spi.audit.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.builder.RouteBuilder;
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

  public static final String IMVP_EXCHANGE = "imvp";
  public static final String NOTIFY_AUTOMATED_CLAIM_QUEUE = "notifyAutomatedClaim";

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  public static final String ENDPOINT_AUDIT_EVENT = "seda:audit-event";

  public static final String ENDPOINT_SLACK_EVENT = "seda:slack-event";

  public static final String ENDPOINT_MAS_COMPLETE = "direct:mas-complete";

  public static final String ENDPOINT_LIGHTHOUSE_EVIDENCE = "direct:lighthouse-claim-submit";
  public static final String ENDPOINT_UPLOAD_PDF = "direct:upload-pdf";
  public static final String ENDPOINT_AUDIT_WIRETAP = "direct:wire";
  private static final String ENDPOINT_COLLECT_EVIDENCE = "direct:collect-evidence";
  public static final String ENDPOINT_NOTIFY_AUDIT = "seda:notify-audit";
  public static final String END_POINT_RFD = "direct:rfd";
  public static final String ENDPOINT_ORDER_EXAM = "direct:order-exam";
  public static final String ENDPOINT_OFFRAMP_ERROR = "direct:offramp-error";

  // Base names for wiretap endpoints
  public static final String MAS_CLAIM_WIRETAP = "mas-claim-submitted";
  public static final String EXAM_ORDER_STATUS_WIRETAP = "exam-order-status";

  private final BipClaimService bipClaimService;

  private final MasProcessingService masProcessingService;

  private final AuditEventService auditEventService;

  private final MasConfig masConfig;

  private final MasPollingProcessor masPollingProcessor;

  private final MasOrderExamProcessor masOrderExamProcessor;

  private final MasAccessErrProcessor masAccessErrProcessor;

  private final MasCollectionService masCollectionService;
  private final MasAssessmentResultProcessor masAssessmentResultProcessor;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  private final EvidenceSummaryDocumentProcessor evidenceSummaryDocumentProcessor;

  private final HealthAssessmentErrCheckProcessor healthAssessmentErrCheckProcessor;

  @Override
  public void configure() {
    configureAuditing();
    configureNotify();
    configureAutomatedClaim();
    configureMasProcessing();
    configureCollectEvidence();
    configureUploadPdf();
    configureCompleteProcessing();
    configureOrderExamStatus();
  }

  private void configureAutomatedClaim() {
    RabbitMqCamelUtils.fromRabbitmq(this, IMVP_EXCHANGE, NOTIFY_AUTOMATED_CLAIM_QUEUE)
        .setExchangePattern(ExchangePattern.InOnly)
        .routeId("mas-request-injection")
        .convertBodyTo(MasAutomatedClaimPayload.class)
        .wireTap(RabbitMqCamelUtils.wiretapProducer(MAS_CLAIM_WIRETAP))
        .to(ENDPOINT_AUTOMATED_CLAIM);

    final String DIRECT_TO_MQ_MAS = "direct:toMq-mas-notification";
    final String MAS_NOTIFICATION_EXCHANGE = "mas-notification-exchange";
    final String MAS_NOTIFICATION_ROUTING_KEY = "mas-notification";
    RabbitMqCamelUtils.addToRabbitmqRoute(
        this,
        DIRECT_TO_MQ_MAS,
        MAS_NOTIFICATION_EXCHANGE,
        MAS_NOTIFICATION_ROUTING_KEY,
        "&requestTimeout=0");

    var checkClaimRouteId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(checkClaimRouteId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        // For the ENDPOINT_AUDIT_WIRETAP, use auditProcessor to convert body to type AuditEvent
        .onPrepare(auditProcessor(checkClaimRouteId, "Checking if claim is ready..."))
        // Msg body is still a MasAutomatedClaimPayload
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .to(DIRECT_TO_MQ_MAS);

    var processClaimRouteId = "mas-claim-processing";
    RabbitMqCamelUtils.fromRabbitmq(this, MAS_NOTIFICATION_EXCHANGE, MAS_NOTIFICATION_ROUTING_KEY)
        .routeId(processClaimRouteId)
        .convertBodyTo(MasAutomatedClaimPayload.class)
        .process(masPollingProcessor)
        .setExchangePattern(ExchangePattern.InOnly); // TODO Q: Why is this needed?
  }

  private void configureMasProcessing() {
    String routeId = "mas-processing";

    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Started claim processing."))
        .process(convertToMasProcessingObject())
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .setProperty("idType", simple("${body.idType}"))
        .to(ENDPOINT_COLLECT_EVIDENCE) // collect evidence from lighthouse and MAS
        // determine if evidence is sufficient
        .routingSlip(method(slipClaimSubmitRouter, "routeHealthSufficiency"))
        .convertBodyTo(AbdEvidenceWithSummary.class)
        .process(masAssessmentResultProcessor)
        .process(new HealthEvidenceProcessor()) // returns MasTransferObject
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking} == false"))
        .to(ENDPOINT_ORDER_EXAM)
        .when(simple("${exchangeProperty.sufficientForFastTracking} == true"))
        .to(END_POINT_RFD)
        .otherwise()
        // Off ramp if the Sufficient For Fast Tracking is null
        .setProperty("offRampError", constant("Sufficiency cannot be determined."))
        .setProperty("sourceRoute", constant("assessorError"))
        .log("Assessor Error. Off-ramping claim")
        .process(masAccessErrProcessor)
        .to(ENDPOINT_OFFRAMP_ERROR)
        .end();

    String rfdRouteId = "mas-rfd";
    String pdfFailError = "docUploadFailed";
    from(END_POINT_RFD)
        // input: MasAutomatedClaimPayload
        .routeId(rfdRouteId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(rfdRouteId, "Sufficient evidence for ready for decision."))
        // Upload PDF
        .doTry()
        .to(ENDPOINT_UPLOAD_PDF)
        .doCatch(BipException.class)
        // Completion code needs the MasProcessingObject as the body.
        .setBody(simple("${exchangeProperty.payload}"))
        .setProperty("offRampError", constant(pdfFailError))
        .setProperty("sourceRoute", constant(rfdRouteId))
        .to(ENDPOINT_OFFRAMP_ERROR)
        .stop()
        .end() // End try
        .to(ENDPOINT_MAS_COMPLETE);

    // Call "Order Exam" in the absence of evidence .i.e Sufficient For Fast Tracking is "false"
    var orderExamRouteId = "mas-order-exam";
    final String orderFailMessage = "examOrderFailed";
    final String pdfFailMessage = "PDF upload failed after exam order requested.";

    from(ENDPOINT_ORDER_EXAM)
        // input: MasAutomatedClaimPayload
        .routeId(orderExamRouteId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            auditProcessor(orderExamRouteId, "There is insufficient evidence. Ordering an exam"))
        .doTry()
        .process(masOrderExamProcessor)
        .log("MAS Order Exam response: ${body}")
        // Upload PDF but catch errors since exam was ordered and continue
        .to(ENDPOINT_UPLOAD_PDF)
        .to(ENDPOINT_MAS_COMPLETE)
        .doCatch(MasException.class)
        // Body is still the Mas Processing object.
        .setProperty("sourceRoute", constant(orderExamRouteId))
        .setProperty("offRampError", constant(orderFailMessage))
        .to(ENDPOINT_OFFRAMP_ERROR)
        .stop() // Offramp and don't continue processing
        .doCatch(BipException.class)
        // Mas Complete Processing code expects this to be the body of the message
        .setBody(simple("${exchangeProperty.payload}"))
        // Wiretap will cause no code to execute after the end of the try. Intentional here.
        .wireTap(ENDPOINT_NOTIFY_AUDIT) // Send error notification to slack
        .onPrepare(slackEventProcessor(orderExamRouteId, pdfFailMessage))
        .to(ENDPOINT_MAS_COMPLETE)
        .end();

    // Wiretap does NOT let camel work as expected when placed directly inside doCatch()
    // Thus it is broken out here, in the interest of letting normal flow/control happen.
    from(ENDPOINT_OFFRAMP_ERROR)
        .wireTap(ENDPOINT_NOTIFY_AUDIT) // Send error notification to slack
        .onPrepare(slackOffRampProcessor())
        .to(ENDPOINT_MAS_COMPLETE)
        .end();
  }

  private void configureCollectEvidence() {
    String lighthouseRetryRoute = "direct:lighthouse-retry";
    String lighthouseRoute = "mas-automated-claim-lighthouse";

    String routeId = "mas-collect-evidence";
    from(ENDPOINT_COLLECT_EVIDENCE)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Collecting evidence"))
        .setProperty("payload", simple("${body}"))
        .multicast(new GroupedExchangeAggregationStrategy())
        .process(
            FunctionProcessor.fromFunction(masCollectionService::collectAnnotations)) // call MAS
        .to(ENDPOINT_LIGHTHOUSE_EVIDENCE) // call lighthouse
        .end() // end multicast
        .process(combineExchangesProcessor()) // returns HealthDataAssessment
        .process(new ServiceLocationsExtractorProcessor()); // put service locations to property

    from(ENDPOINT_LIGHTHOUSE_EVIDENCE)
        .routeId(lighthouseRoute)
        .process(payloadToClaimProcessor())
        .to(lighthouseRetryRoute)
        // Handle the errors to permit processing to continue
        .onException(ExchangeTimedOutException.class, ExternalCallException.class)
        .handled(true)
        .wireTap(ENDPOINT_NOTIFY_AUDIT) // Send error notification to slack
        .onPrepare(
            slackEventPropertyProcessor(
                lighthouseRoute, "Lighthouse health data not retrieved.", "payload"))
        .process(lighthouseContinueProcessor()) // But keep processing
        .end(); // End of onException

    from(lighthouseRetryRoute)
        .doTry()
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .convertBodyTo(HealthDataAssessment.class)
        .process(healthAssessmentErrCheckProcessor) // Check for errors, and throw or do not alter
        .endDoTry()
        .doCatch(ExchangeTimedOutException.class, ExternalCallException.class)
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .convertBodyTo(HealthDataAssessment.class)
        .process(healthAssessmentErrCheckProcessor)
        .endDoCatch();
  }

  private void configureUploadPdf() {
    var routeId = "mas-upload-pdf";
    from(ENDPOINT_UPLOAD_PDF)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Generating PDF"))
        .process(generatePdfProcessor()) // convert to PDF payload
        .process(evidenceSummaryDocumentProcessor) // store evidence in DB
        .to(PrimaryRoutes.ENDPOINT_GENERATE_FETCH_PDF)
        .process(convertToPdfResponse())
        .process(
            exchange -> {
              var pdfResponse = exchange.getMessage().getBody(FetchPdfResponse.class);
              var masProcessingObject = exchange.getProperty("payload", MasProcessingObject.class);
              bipClaimService.uploadPdf(masProcessingObject.getClaimPayload(), pdfResponse);
            })
        .setBody(simple("${exchangeProperty.payload}"))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Uploaded PDF"));
  }

  private void configureOrderExamStatus() {
    // This route does not do anything, but an audit event is persisted
    String routeId = "mas-exam-order-status";
    from(ENDPOINT_EXAM_ORDER_STATUS)
        .routeId(routeId)
        .wireTap(RabbitMqCamelUtils.wiretapProducer(EXAM_ORDER_STATUS_WIRETAP))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Exam Order Status Called"))
        .log("Invoked " + routeId);
  }

  private void configureCompleteProcessing() {
    var routeId = "mas-complete-claim";
    from(ENDPOINT_MAS_COMPLETE)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Updating claim and contentions"))
        .process(
            MasIntegrationProcessors.completionProcessor(routeId, bipClaimService, masProcessingService))
        .choice()
        .when(simple("${exchangeProperty.completionSlackMessage} != null"))
        .wireTap(ENDPOINT_NOTIFY_AUDIT)
        .onPrepare(slackEventPropertyProcessor(routeId, "completionSlackMessage"))
        .endChoice()
        .otherwise()
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Successful processing"))
        .end();
  }

  private void configureNotify() {
    from(ENDPOINT_NOTIFY_AUDIT)
        .routeId("vro-notify")
        .multicast()
        .to(ENDPOINT_SLACK_EVENT)
        .to(ENDPOINT_AUDIT_EVENT);
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

    // Capture audit events
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

    String webhook = masConfig.getSlackExceptionWebhook();
    String channel = masConfig.getSlackExceptionChannel();
    String slackRoute = String.format("slack:#%s?webhookUrl=%s", channel, webhook);
    log.info("Routing to slack: {}", slackRoute);
    from(ENDPOINT_SLACK_EVENT)
        .routeId("mas-slack-event")
        .filter(exchange -> StringUtils.isNotBlank(webhook))
        .process(FunctionProcessor.fromFunction(AuditEvent::toString))
        .to(slackRoute);
  }
}
