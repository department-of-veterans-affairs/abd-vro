package gov.va.vro.service.provider.camel;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceRoutes extends RouteBuilder {
  private final CamelUtils camelUtils;

  @Override
  public void configure() {
    camelUtils.asyncSedaEndpoint("seda:logToFile");
    camelUtils.asyncSedaEndpoint("seda:claim-router");

    camelUtils.asyncSedaEndpoint("seda:claim-vro-processed");
    camelUtils.multiConsumerSedaEndpoint("seda:claim-vro-processed");
    from("seda:claim-vro-processed").log(">>>>>>>>> VRO processed! claim: ${body.toString()}");

    from("seda:logToFile").marshal().json().log(">>2> ${body.getClass()}").to("file://target/post");

    from("direct:postClaim")
        .log(">>1> ${body.getClass()}")
        // save Claim to DB and assign UUID before anything else
        .bean(CamelClaimService.class, "addClaim")
        // https://camel.apache.org/components/3.16.x/eips/recipientList-eip.html#_using_parallel_processing
        .recipientList(constant("seda:logToFile,seda:claim-router"))
        .parallelProcessing()
        .log(">>5> ${body.toString()}");
  }
}
