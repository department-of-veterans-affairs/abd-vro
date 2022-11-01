package gov.va.vro.service.provider.camel;

import gov.va.vro.model.event.JsonConverter;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.event.AuditEventProcessor;
import gov.va.vro.service.provider.MasPollingProcessor;
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

  private final MasPollingProcessor masPollingProcessor;

  private final AuditEventProcessor auditEventProcessor;

  @Override
  public void configure() {
    configureExceptionHandling();
    configureAutomatedClaim();
    configureOrderExamStatus();
  }

  private void configureAutomatedClaim() {
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId("mas-claim-notification")
        .process(
            auditEventProcessor.event(
                "mas-claim-notification",
                "Setting a delay before staring Automated claim processing."))
        .delay(header(MAS_DELAY_PARAM))
        .setExchangePattern(ExchangePattern.InOnly)
        .process(
            auditEventProcessor.event("mas-claim-notification", "Calling endpoint " + ENDPOINT_MAS))
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
