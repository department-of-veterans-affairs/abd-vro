package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.processors.MockRemoteService;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** Defines primary routes */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryRoutes extends RouteBuilder {

  public static final String ENDPOINT_PROCESS_CLAIM = "direct:processClaim";
  public static final String ENDPOINT_LOG_TO_FILE = "seda:logToFile";
  public static final String ENDPOINT_ASSESS_CLAIM = "direct:assess";

  private final CamelUtils camelUtils;
  private final SaveToDbService saveToDbService;

  @Override
  public void configure() {
    configureRouteFileLogger();
    configureRoutePostClaim();
    configureRouteProcessClaim();
    configureRouteClaimSubmit(); // ToDo remove in favor of RouteProcessClaim
    configureRouteClaimProcessed();

    configureRouteGeneratePdf();
    configureRouteFetchPdf();
    // TODO: leaving them as examples, but they should be removed in a subsequent PR
    //    configureRouteHealthDataAssessor();
  }

  private void configureRouteFileLogger() {
    camelUtils.asyncSedaEndpoint(ENDPOINT_LOG_TO_FILE);
    from(ENDPOINT_LOG_TO_FILE)
        .marshal()
        .json()
        .log(">>2> ${body.getClass()}")
        .to("file://target/post");
  }

  private void configureRouteProcessClaim() {
    from(ENDPOINT_PROCESS_CLAIM)
        .process(FunctionProcessor.fromFunction(saveToDbService::insertClaim))
        .to(ENDPOINT_LOG_TO_FILE)
        .to(ENDPOINT_ASSESS_CLAIM)
        .log(">>5> ${body.toString()}");
    // TODO: insert a post processing step here to update the DB with results from services

    // Rabbit calls to processing services go here
    from(ENDPOINT_ASSESS_CLAIM).bean(new MockRemoteService("Assess claim"), "processClaim");
  }

  private void configureRoutePostClaim() {
    from("direct:postClaim")
        .log(">>1> ${body.getClass()}")
        // save Claim to DB and assign UUID before anything else
        .bean(CamelClaimService.class, "addClaim")
        // https://camel.apache.org/components/3.16.x/eips/recipientList-eip.html#_using_parallel_processing
        .recipientList(constant("seda:logToFile,seda:claim-router"))
        .parallelProcessing()
        .log(">>5> ${body.toString()}");
  }

  private void configureRouteClaimProcessed() {
    camelUtils.asyncSedaEndpoint("seda:claim-vro-processed");
    camelUtils.multiConsumerSedaEndpoint("seda:claim-vro-processed");
    from("seda:claim-vro-processed").log(">>>> VRO processed! claim: ${body.toString()}");
  }

  SampleData sampleData = new SampleData();

  private void configureRouteClaimSubmit() {
    // send JSON-string payload to RabbitMQ
    from("direct:claim-submit")
        .routeId("claim-submit")
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .routingSlip(method(SlipClaimSubmitRouter.class, "routeClaimSubmit"));
  }

  private void configureRouteGeneratePdf() {
    String exchangeName = "pdf_generator";
    String queueName = "generate_pdf";

    // send JSON-string payload to RabbitMQ
    from("direct:generate_pdf")
        .routeId("generate_pdf")

        // if veteranInfo is empty, load a samplePayload for it
        .choice()
        .when(simple("${body.veteranInfo} == null"))
        .setBody(
            exchange ->
                sampleData.sampleGeneratePdfPayload(exchange.getMessage(GeneratePdfPayload.class)))
        .end()
        .to("rabbitmq:" + exchangeName + "?routingKey=" + queueName);
  }

  private void configureRouteFetchPdf() {
    String exchangeName = "pdf_generator";
    String queueName = "fetch_pdf";

    // send JSON-string payload to RabbitMQ
    from("direct:fetch_pdf")
        .routeId("fetch_pdf")
        .to("rabbitmq:" + exchangeName + "?routingKey=" + queueName);
  }
}
