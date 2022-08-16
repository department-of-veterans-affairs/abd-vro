package gov.va.vro.service.provider.camel;

import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** Defines primary routes */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryRoutes extends RouteBuilder {

  public static final String ENDPOINT_SUBMIT_CLAIM = "direct:claim-submit";
  public static final String ENDPOINT_SUBMIT_CLAIM_FULL = "direct:claim-submit-full";
  public static final String ENDPOINT_LOG_TO_FILE = "seda:logToFile";

  private final CamelUtils camelUtils;
  private final SaveToDbService saveToDbService;

  @Override
  public void configure() {
    configureRouteFileLogger();
    configureRouteClaimSubmit();
    configureRouteClaimSubmitForFull();
    configureRouteClaimProcessed();

    configureRouteGeneratePdf();
    configureRouteFetchPdf();
  }

  private void configureRouteFileLogger() {
    camelUtils.asyncSedaEndpoint(ENDPOINT_LOG_TO_FILE);
    from(ENDPOINT_LOG_TO_FILE)
        .marshal()
        .json()
        .log(">>2> ${body.getClass()}")
        .to("file://target/post");
  }

  private void configureRouteClaimProcessed() {
    camelUtils.asyncSedaEndpoint("seda:claim-vro-processed");
    camelUtils.multiConsumerSedaEndpoint("seda:claim-vro-processed");
    from("seda:claim-vro-processed").log(">>>> VRO processed! claim: ${body.toString()}");
  }

  private void configureRouteClaimSubmit() {
    // send JSON-string payload to RabbitMQ
    from(ENDPOINT_SUBMIT_CLAIM)
        .routeId("claim-submit")
        .process(FunctionProcessor.fromFunction(saveToDbService::insertClaim))
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmit"))
        .log(">>5> ${body}");
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
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmitFull"))
        .log(">>5> ${body}");
  }

  private void configureRouteGeneratePdf() {

    String exchangeName = "pdf-generator";
    String queueName = "wassszx";

    // send JSON-string payload to RabbitMQ
    from("direct:generate-pdf")
        .routeId("generate-pdf")
        .to("rabbitmq:" + exchangeName + "?routingKey=" + queueName);
  }

  private void configureRouteFetchPdf() {
    String exchangeName = "pdf-generator";
    String queueName = "fetch-pdf";

    // send JSON-string payload to RabbitMQ
    from("direct:fetch-pdf")
        .routeId("fetch-pdf")
        .to("rabbitmq:" + exchangeName + "?routingKey=" + queueName);
  }
}
