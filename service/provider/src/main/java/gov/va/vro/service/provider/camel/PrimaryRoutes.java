package gov.va.vro.service.provider.camel;

import gov.va.vro.service.spi.demo.model.AssessHealthData;
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
  public static final String CHANNEL_SEDA_LOG_TO_FILE = "seda:logToFile";
  public static final String CHANNEL_DIRECT_POST_CLAIM = "direct:postClaim";
  private final CamelUtils camelUtils;

  @Override
  public void configure() {
    configureRouteFileLogger();
    configureRoutePostClaim();
    configureRouteClaimProcessed();

    // configureRouteHealthDataAssessor();
    // configureRoutePdfGenerator();
  }

  private void configureRouteFileLogger() {
    camelUtils.asyncSedaEndpoint(CHANNEL_SEDA_LOG_TO_FILE);
    from(CHANNEL_SEDA_LOG_TO_FILE)
        .marshal()
        .json()
        .log(">>2> ${body.getClass()}")
        .to("file://target/post");
  }

  private void configureRoutePostClaim() {
    from(CHANNEL_DIRECT_POST_CLAIM)
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

  private void configureRouteHealthDataAssessor() {
    String queueName = "health_data_assessor";

    // send JSON-string payload to RabbitMQ
    from("direct:assess_health_data_demo")
        .routeId("assess_health_data_demo")
        // .tracing()

        // if bpObservations is empty, load a samplePayload for it
        .choice()
        // https://camel.apache.org/components/3.11.x/languages/simple-language.html
        .when(simple("${body.bpObservations} == null"))
        .setBody(
            exchange ->
                sampleData.sampleAssessHealthPayload(exchange.getMessage(AssessHealthData.class)))
        .end()

        // .log(">>> To assess_health_data: ${body}")
        // .to("log:INFO?showBody=true&showHeaders=true")

        // https://camel.apache.org/components/3.11.x/rabbitmq-component.html
        // Subscribers of this RabbitMQ queue expect a JSON string
        // Since the RabbitMQ endpoint accepts a byte[] for the message,
        // CamelDtoConverter will automatically marshal AssessHealthData into a JSON string encoded
        // as a byte[]
        .to("rabbitmq:assess_health_data?routingKey=" + queueName);
  }

  private void configureRoutePdfGenerator() {
    String exchangeName = "generate_pdf";
    String queueName = "pdf_generator";

    // send JSON-string payload to RabbitMQ
    from("direct:generate_pdf_demo")
        .routeId("generate_pdf_demo")

        // if patientInfo is empty, load a samplePayload for it
        .choice()
        .when(simple("${body.patientInfo} == null"))
        .setBody(
            exchange ->
                sampleData.sampleGeneratePdfPayload(exchange.getMessage(GeneratePdfPayload.class)))
        .end()
        .to("rabbitmq:" + exchangeName + "?routingKey=" + queueName);
  }
}
