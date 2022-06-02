package gov.va.vro.service.provider.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Defines primary routes
 */
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

  private void configureRouteFileLogger(){
    camelUtils.asyncSedaEndpoint("seda:logToFile");
    from("seda:logToFile").marshal().json().log(">>2> ${body.getClass()}").to("file://target/post");
  }

  private void configureRoutePostClaim(){
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
    // send JSON-string payload to RabbitMQ
    JSONObject payload = samplePayload();
    log.debug("PAYLOAD: {}", payload.toJSONString());
  }

  private JSONObject samplePayload() {
    JSONObject payload = new JSONObject();
    payload.put("contention", "hypertension");
    payload.put("bp_observations", sampleLighthouseObservationResponse());
    return payload;
  }

  private JSONObject sampleLighthouseObservationResponse() {
    JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
    try {
      InputStream filestream = getClass().getResourceAsStream("/examples/lighthouse_observations_resp.json");
      if(filestream==null) return null;
      return (JSONObject) parser.parse(filestream);
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
