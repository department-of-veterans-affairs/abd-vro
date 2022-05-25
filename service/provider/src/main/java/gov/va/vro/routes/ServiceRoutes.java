package gov.va.vro.routes;

import gov.va.vro.services.ClaimService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceRoutes extends RouteBuilder {
  @Autowired private CamelUtils camelUtils;

  @Override
  public void configure() {
    camelUtils.asyncSedaEndpoint("seda:logToFile");
    camelUtils.asyncSedaEndpoint("seda:claim-router");

    from("seda:logToFile").marshal().json().log(">>2> ${body.getClass()}").to("file://target/post");

    from("direct:postClaim")
        .log(">>1> ${body.getClass()}")
        // save Claim to DB and assign UUID before anything else
        .bean(ClaimService.class, "addClaim")
        // https://camel.apache.org/components/3.16.x/eips/recipientList-eip.html#_using_parallel_processing
        .recipientList( // TODO: convert to constant
            constant("seda:logToFile,seda:claim-router"))
        .parallelProcessing()
        .log(">>5> ${body.toString()}");
  }
}
