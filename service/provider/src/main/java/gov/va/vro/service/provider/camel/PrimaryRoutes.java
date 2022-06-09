package gov.va.vro.service.provider.camel;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/** Defines primary routes */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryRoutes extends RouteBuilder {
  private final CamelUtils camelUtils;

  @Override
  public void configure() {
    configureRouteFileLogger();
    configureRoutePostClaim();
    configureRouteClaimProcessed();

    configureRouteHealthDataAssessor();
  }

  private void configureRouteFileLogger() {
    camelUtils.asyncSedaEndpoint("seda:logToFile");
    from("seda:logToFile").marshal().json().log(">>2> ${body.getClass()}").to("file://target/post");
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

  private void configureRouteHealthDataAssessor() {
    String queue_name = "health_data_assessor";

    // send JSON-string payload to RabbitMQ
    from("direct:assess_health_data_demo")
        .routeId("assess_health_data_demo")
        // .tracing()

        // if bpObservations is empty, load a samplePayload for it
        .choice()
        // https://camel.apache.org/components/3.11.x/languages/simple-language.html
        .when(simple("${body.bpObservations} == null"))
        .setBody(exchange -> samplePayload(exchange.getMessage(AssessHealthData.class)))
        .end()

        // .log(">>> To assess_health_data: ${body}")
        // .to("log:INFO?showBody=true&showHeaders=true")

        // https://camel.apache.org/components/3.11.x/rabbitmq-component.html
        // Subscribers of this RabbitMQ queue expect a JSON string
        // Since the RabbitMQ endpoint accepts a byte[] for the message,
        // CamelDtoConverter will automatically marshal AssessHealthData into a JSON string encoded
        // as a byte[]
        .to("rabbitmq:assess_health_data?routingKey=" + queue_name);
  }

  private AssessHealthData samplePayload(AssessHealthData payload) {
    log.info("Using sample Lighthouse Observation Response string");
    if (payload == null) {
      payload = new AssessHealthData();
      payload.setContention("hypertension");
    }
    payload.setBpObservations(sampleLighthouseObservationResponse().toJSONString());
    return payload;
  }

  // memoize so we don't hit the URL too often
  private static String sampleLhObservationResponseString;

  private String sampleLhObservationResponseString() throws IOException {
    if (sampleLhObservationResponseString == null) {
      String sampleResponseUrl =
          "https://gist.githubusercontent.com/yoomlam/"
              + "0e22b8d01f6fd1bd51d6912dd051fda9/raw/9c45a1372f364b54a8e531aaf7d7f0d83c86e961/"
              + "lighthouse_observations_resp.json";
      log.info("Retrieving sample Lighthouse Observation response");
      BufferedInputStream in = new BufferedInputStream(new URL(sampleResponseUrl).openStream());
      sampleLhObservationResponseString = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
    }
    return sampleLhObservationResponseString;
  }

  private JSONObject sampleLighthouseObservationResponse() {
    JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
    try {
      return (JSONObject) parser.parse(sampleLhObservationResponseString());
    } catch (ParseException | IOException e) {
      log.warn("", e);
      return null;
    }
  }
}
