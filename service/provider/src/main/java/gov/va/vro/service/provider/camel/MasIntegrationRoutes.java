package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.event.JsonConverter;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.event.AuditEventProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

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

  private void configureMasProcessing() {
    String routeId = "mas-processing";
    from(ENDPOINT_MAS_PROCESSING)
        .routeId(routeId)
        .process(auditEventProcessor.event(routeId, "Calling Collect Annotations"))
        // Call Mas API to collect annotations
        .process(FunctionProcessor.fromFunction(masCollectionService::collectAnnotations))
        // TODO:  call Lighthouse
        .process(auditEventProcessor.event(routeId, "Completed Collect Annotations"))
        // TODO: call pcOrderExam in the absence of evidence
        // Generate PDF
        .to(PrimaryRoutes.ENDPOINT_GENERATE_PDF);
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
