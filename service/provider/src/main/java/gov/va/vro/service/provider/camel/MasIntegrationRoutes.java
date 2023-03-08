package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.auditProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.combineExchangesProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.convertToMasProcessingObject;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.convertToPdfResponse;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.generatePdfProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.payloadToClaimProcessor;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.MasAccessErrProcessor;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasOrderExamProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.services.EvidenceSummaryDocumentProcessor;
import gov.va.vro.service.provider.services.HealthEvidenceProcessor;
import gov.va.vro.service.provider.services.MasAssessmentResultProcessor;
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

  public static final String IMVP_EXCHANGE = "imvp";
  public static final String NOTIFY_AUTOMATED_CLAIM_QUEUE = "notifyAutomatedClaim";
  public static final String ENDPOINT_REQUEST_INJECTION =
      RabbitMqCamelUtils.rabbitmqConsumerEndpoint(IMVP_EXCHANGE, NOTIFY_AUTOMATED_CLAIM_QUEUE);

  public static final String ENDPOINT_EXAM_ORDER_STATUS = "direct:exam-order-status";

  public static final String MAS_DELAY_PARAM = "masDelay";

  public static final String MAS_RETRY_PARAM = "masRetryCount";

  public static final String ENDPOINT_MAS_PROCESSING = "direct:mas-processing";

  public static final String ENDPOINT_AUDIT_EVENT = "seda:audit-event";

  public static final String ENDPOINT_SLACK_EVENT = "seda:slack-event";

  public static final String ENDPOINT_MAS_COMPLETE = "direct:mas-complete";

  public static final String ENDPOINT_UPLOAD_PDF = "direct:upload-pdf";
  public static final String ENDPOINT_AUDIT_WIRETAP = "direct:wire";
  private static final String ENDPOINT_COLLECT_EVIDENCE = "direct:collect-evidence";
  public static final String ENDPOINT_OFFRAMP = "seda:offramp";

  public static final String ENDPOINT_ORDER_EXAM = "direct:order-exam";

  public static final String ENDPOINT_ACCESS_ERR = "direct:assessorError";

  // Base names for wiretap endpoints
  public static final String MAS_CLAIM_WIRETAP = "mas-claim-submitted";
  public static final String EXAM_ORDER_STATUS_WIRETAP = "exam-order-status";

  private final BipClaimService bipClaimService;

  private final AuditEventService auditEventService;

  private final MasConfig masConfig;

  private final MasPollingProcessor masPollingProcessor;

  private final MasOrderExamProcessor masOrderExamProcessor;

  private final MasAccessErrProcessor masAccessErrProcessor;

  private final MasCollectionService masCollectionService;
  private final MasAssessmentResultProcessor masAssessmentResultProcessor;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  private final EvidenceSummaryDocumentProcessor evidenceSummaryDocumentProcessor;

  @Override
  public void configure() {
    configureAuditing();
    configureOffRamp();
    configureAutomatedClaim();
    configureMasProcessing();
    configureCollectEvidence();
    configureUploadPdf();
    configureCompleteProcessing();
    configureOrderExamStatus();
  }

  private void configureAutomatedClaim() {
    from(ENDPOINT_REQUEST_INJECTION)
        .setExchangePattern(ExchangePattern.InOnly)
        .routeId("mas-request-injection")
        .log("A ${headers} ${body}")
        .convertBodyTo(MasAutomatedClaimPayload.class)
        .log("B ${headers} ${body}")
        .to(ENDPOINT_AUTOMATED_CLAIM);

    var checkClaimRouteId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(checkClaimRouteId)
        .log("1 ${headers} ${body}")
        // Clear the CamelRabbitmqExchangeName and CamelRabbitmqRoutingKey so it doesn't interfere
        // with future sending to rabbitmq endpoints
        // https://stackoverflow.com/a/50087665
        // https://users.camel.apache.narkive.com/weJH1I5T/camel-rabbitmq#post4
        .removeHeaders("CamelRabbitmq*")
        // .convertBodyTo(MasAutomatedClaimPayload.class)
        // .log("2 ${headers} ${body}")
        // .convertBodyTo(byte[].class)
        // .convertBodyTo(String.class)
        // .log("3 ${headers} ${body}")
        // .convertBodyTo(byte[].class)
        // .convertBodyTo(MasAutomatedClaimPayload.class)
        .wireTap(VroCamelUtils.wiretapProducer(MAS_CLAIM_WIRETAP))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        // For the ENDPOINT_AUDIT_WIRETAP, use auditProcessor to convert body to type AuditEvent
        .onPrepare(auditProcessor(checkClaimRouteId, "Checking if claim is ready..."))
        // Msg body is still a MasAutomatedClaimPayload
        .log("6 ${headers} ${body}")
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .to(ENDPOINT_MAS);

    var processClaimRouteId = "mas-claim-processing";
    from(ENDPOINT_MAS)
        .routeId(processClaimRouteId)
        // TODO Q: Why is unmarshal needed? Isn't the msg body already a MasAutomatedClaimPayload?
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .log("7 ${headers} ${body}")
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
        // TODO remove unmarshal calls if possible: unmarshalling should be automatic
        .unmarshal(new JacksonDataFormat(AbdEvidenceWithSummary.class))
        .process(masAssessmentResultProcessor)
        .process(new HealthEvidenceProcessor()) // returns MasTransferObject
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking} == false"))
        // Order Exam only if the Sufficient For Fast Tracking is "false"
        .to(ENDPOINT_ORDER_EXAM)
        // Upload PDF only if SufficientForFastTracking is either true or false
        .to(ENDPOINT_UPLOAD_PDF)
        // Check and update statuses
        .to(ENDPOINT_MAS_COMPLETE)
        .when(simple("${exchangeProperty.sufficientForFastTracking} == true"))
        // Upload PDF only if SufficientForFastTracking is either true or false
        .to(ENDPOINT_UPLOAD_PDF)
        // Check and update statuses
        .to(ENDPOINT_MAS_COMPLETE)
        .otherwise()
        // Off ramp if the Sufficient For Fast Tracking is null
        .to(ENDPOINT_ACCESS_ERR)
        .end();

    // Call "Order Exam" in the absence of evidence .i.e Sufficient For Fast Tracking is "false"
    var orderExamRouteId = "mas-order-exam";
    from(ENDPOINT_ORDER_EXAM)
        // input: MasAutomatedClaimPayload
        .routeId(orderExamRouteId)
        .choice()
        .when(simple("${exchangeProperty.sufficientForFastTracking} == false"))
        .process(masOrderExamProcessor)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            auditProcessor(orderExamRouteId, "There is insufficient evidence. Ordering an exam"))
        .log("MAS Order Exam response: ${body}")
        .end();

    // Off Ramp if the Sufficiency can't be determined .i.e. sufficientForFastTracking is 'null'
    var assessorErrorRouteId = "assessorError";
    from(ENDPOINT_ACCESS_ERR)
        .routeId(assessorErrorRouteId)
        .log("Assessor Error. Off-ramping claim")
        .process(masAccessErrProcessor)
        .wireTap(ENDPOINT_OFFRAMP)
        .onPrepare(auditProcessor(assessorErrorRouteId, "Sufficiency cannot be determined"))
        .to(ENDPOINT_MAS_COMPLETE);
  }

  private void configureCollectEvidence() {
    String lighthouseEndpoint = "direct:lighthouse-claim-submit";

    String routeId = "mas-collect-evidence";
    from(ENDPOINT_COLLECT_EVIDENCE)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Collecting evidence"))
        .setProperty("payload", simple("${body}"))
        .multicast(new GroupedExchangeAggregationStrategy())
        .process(
            FunctionProcessor.fromFunction(masCollectionService::collectAnnotations)) // call MAS
        .to(lighthouseEndpoint) // call Lighthouse
        .end() // end multicast
        .process(combineExchangesProcessor()) // returns HealthDataAssessment
        .process(new ServiceLocationsExtractorProcessor()); // put service locations to property

    from(lighthouseEndpoint)
        .routeId("mas-automated-claim-lighthouse")
        .process(payloadToClaimProcessor())
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .unmarshal(new JacksonDataFormat(HealthDataAssessment.class));
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
        .wireTap(VroCamelUtils.wiretapProducer(EXAM_ORDER_STATUS_WIRETAP))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Exam Order Status Called"))
        .log("Invoked " + routeId);
  }

  private void configureCompleteProcessing() {
    var routeId = "mas-complete-claim";
    from(ENDPOINT_MAS_COMPLETE)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Removing Special Issue"))
        .bean(FunctionProcessor.fromFunction(bipClaimService::removeSpecialIssue))
        .choice()
        // Mark the claim as "RFD" only if the Sufficient For Fast Tracking is "true"
        .when(simple("${exchangeProperty.sufficientForFastTracking} == true"))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Sufficient evidence for fast tracking. Marking as RFD"))
        .bean(FunctionProcessor.fromFunction(bipClaimService::markAsRfd))
        .endChoice()
        .end()
        .process(FunctionProcessor.fromFunction(bipClaimService::completeProcessing))
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(
            auditProcessor(
                routeId,
                auditable -> {
                  MasProcessingObject mpo = (MasProcessingObject) auditable;
                  return mpo.isTSOJ()
                      ? "Claim satisfies TSOJ condition. Updated status."
                      : "Claim does not satisfy TSOJ condition. Status not updated.";
                }));
  }

  private void configureOffRamp() {
    from(ENDPOINT_OFFRAMP)
        .routeId("mas-offramp")
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
