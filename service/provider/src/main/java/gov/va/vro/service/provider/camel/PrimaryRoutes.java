package gov.va.vro.service.provider.camel;

import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** Defines primary routes. */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryRoutes extends RouteBuilder {

  public static final String ENDPOINT_SUBMIT_CLAIM = "direct:claim-submit";
  public static final String ENDPOINT_SUBMIT_CLAIM_FULL = "direct:claim-submit-full";
  public static final String ENDPOINT_GENERATE_PDF = "direct:generate-pdf";
  public static final String ENDPOINT_FETCH_PDF = "direct:fetch-pdf";

  public static final String ENDPOINT_AUTOMATED_CLAIM = "direct:automated-claim";

  private static final String PDF_EXCHANGE = "pdf-generator";
  private static final String GENERATE_PDF_QUEUE = "generate-pdf";
  private static final String FETCH_PDF_QUEUE = "fetch-pdf";

  private final SaveToDbService saveToDbService;

  @Override
  public void configure() {
    configureRouteClaimSubmit();
    configureRouteClaimSubmitForFull();
    configureRouteGeneratePdf();
    configureRouteFetchPdf();
    configureAutomatedClaim();
  }

  private void configureRouteClaimSubmit() {
    // send JSON-string payload to RabbitMQ
    from(ENDPOINT_SUBMIT_CLAIM)
        .routeId("claim-submit")
        .process(FunctionProcessor.fromFunction(saveToDbService::insertClaim))
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmit"));
  }

  private void configureRouteClaimSubmitForFull() {
    // send JSON-string payload to RabbitMQ
    from(ENDPOINT_SUBMIT_CLAIM_FULL)
        .routeId("claim-submit-full")
        .process(FunctionProcessor.fromFunction(saveToDbService::insertClaim))
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmit"))
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmitFull"));
  }

  private void configureRouteGeneratePdf() {
    from(ENDPOINT_GENERATE_PDF).routeId("generate-pdf").to(pdfRoute(GENERATE_PDF_QUEUE));
  }

  private void configureRouteFetchPdf() {
    from(ENDPOINT_FETCH_PDF).routeId("fetch-pdf").to(pdfRoute(FETCH_PDF_QUEUE));
  }

  private void configureAutomatedClaim() {
    from(ENDPOINT_AUTOMATED_CLAIM)
        .routeId("automated-claim")
        .to("rabbitmq:mas-notification-exchange?routingKey=mas-notification");

    from("rabbitmq:mas-notification-exchange?routingKey=mas-notification")
        .process(
            new Processor() {
              @Override
              public void process(Exchange exchange) {
                log.info("******" + exchange.getMessage());
              }
            });
  }

  private String pdfRoute(String queueName) {
    return String.format("rabbitmq:%s?routingKey=%s", PDF_EXCHANGE, queueName);
  }
}
