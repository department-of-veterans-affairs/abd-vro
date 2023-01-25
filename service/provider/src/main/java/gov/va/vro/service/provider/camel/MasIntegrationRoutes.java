package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.*;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasOrderExamProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.services.EvidenceSummaryDocumentProcessor;
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
  private static final String ENDPOINT_COLLECT_EVIDENCE = "direct:collect-evidence";
  public static final String ENDPOINT_OFFRAMP = "seda:offramp";

  private final BipClaimService bipClaimService;

  private final AuditEventService auditEventService;

  private final MasConfig masConfig;

  private final MasPollingProcessor masPollingProcessor;

  private final MasOrderExamProcessor masOrderExamProcessor;

  private final MasCollectionService masCollectionService;

  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  private final EvidenceSummaryDocumentProcessor evidenceSummaryDocumentProcessor;

  @Override
  public void configure() {
    configureAuditing();
    configureAutomatedClaim();
    configureMasProcessing();
    configureCollectEvidence();
    configureOrderExamStatus();
    configureCompleteProcessing();
    configureUploadPdf();
    configureOffRamp();
  }

  private void configureAutomatedClaim() {
    var checkClaimRouteId = "mas-claim-notification";
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId(checkClaimRouteId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(checkClaimRouteId, "Checking if claim is ready..."))
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .to(ENDPOINT_MAS);

    var processClaimRouteId = "mas-claim-processing";
    from(ENDPOINT_MAS)
        .routeId(processClaimRouteId)
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .process(masPollingProcessor)
        .setExchangePattern(ExchangePattern.InOnly);
  }

  private void configureMasProcessing() {
    String routeId = "mas-processing";

    String orderExamEndpoint = "direct:order-exam";

    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .wireTap(ENDPOINT_AUDIT_WIRETAP)
        .onPrepare(auditProcessor(routeId, "Started claim processing."))
        .process(convertToMasProcessingObject())
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .to(ENDPOINT_COLLECT_EVIDENCE) // collect evidence from lighthouse and MAS
        // determine if evidence is sufficient
        .routingSlip(method(slipClaimSubmitRouter, "routeHealthSufficiency"))
        .unmarshal(new JacksonDataFormat(AbdEvidenceWithSummary.class))
        .process(new HealthEvidenceProcessor()) // returns MasTransferObject
        // Conditionally order exam
        .to(orderExamEndpoint)
        // Upload PDF
        .to(ENDPOINT_UPLOAD_PDF)
        // Check and update statuses
        .to(ENDPOINT_MAS_COMPLETE);

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
            auditProcessor(orderExamRouteId, "There is insufficient evidence. Ordering an exam"))
        .log("MAS Order Exam response: ${body}")
        .end();
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
        .process(combineExchangesProcessor()); // returns HealthDataAssessment

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
        .when(simple("${exchangeProperty.sufficientForFastTracking}"))
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
