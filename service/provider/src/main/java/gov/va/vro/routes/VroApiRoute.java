package gov.va.vro.routes;

import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.model.Payload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.services.ClaimService;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
/** Not for production use */
public class VroApiRoute extends RouteBuilder {
  @Autowired private CamelUtils camelUtils;

  @Override
  public void configure() throws Exception {
    System.out.println(getClass() + ": " + restConfiguration());

    // Could be replaced with
    // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-AddingREST
    rest("/claims")
        // Declares API expectations in results of /api-doc endpoint
        .consumes(MediaType.APPLICATION_JSON)
        .produces(MediaType.APPLICATION_JSON)

        // POST
        .post("/")
        .description("Add claim")
        .type(ClaimSubmissionEntity.class)
        .outType(ClaimSubmissionEntity.class)
        .route()
        .routeId("rest-POST-claim")
        .tracing()
        //        .to("seda:postClaim")
        .bean(CamelEntrance.class, "postClaim")
        .endRest()

        // GET
        .get("/")
        .description("Get all claims")
        .outType(ClaimSubmissionEntity[].class)
        .route()
        .routeId("claims-getAll")
        .bean(ClaimService.class, "getAllClaims")
        .endRest()

        // GET
        .get("/{id}")
        .description("Get claim")
        .outType(ClaimSubmissionEntity.class)
        .route()
        .routeId("claims-getById")
        // https://camel.apache.org/components/3.14.x/languages/simple-language.html#_variables
        .setBody(simple("${header.id}"))
        .bean(ClaimService.class, "getClaim")
        .log(">>3> ${body.toString()}")
        .endRest()

        // GET details
        .get("/details/{id}")
        .description("Get claim")
        .outType(ClaimSubmissionEntity.class)
        .route()
        .routeId("claimDetails-getById")
        .bean(ClaimService.class, "claimDetail")
        .endRest()

        // GET
        .get("/{id}/status-diff-from/{status}")
        .description("Returns the claim when it changes from specified 'status'")
        .outType(ClaimSubmissionEntity.class)
        .route()
        .routeId("claim-status-change")
        .setBody(simple("${header.id}"))
        // subscribe on queue, waiting for specified claim to complete
        .pollEnrich(
            simple("seda:claim-vro-processed-${header.id}?multipleConsumers=true"),
            -1,
            new ChooseSecondExchangeStrategy(),
            false)
        .log(">>5> diff status?: ${body}")
        .convertBodyTo(Payload.class)
        .endRest();

    camelUtils.multiConsumerSedaEndpoint("seda:claim-vro-processed");
    from("seda:claim-vro-processed").log(">>>>>>>>> VRO processed! claim: ${body.toString()}");
  }

  public class ChooseSecondExchangeStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
      Object newBody = newExchange.getIn().getBody();
      System.out.println("------------- ChooseSecondExchangeStrategy: " + newBody);
      if (newExchange == null) return oldExchange;

      return newExchange;
    }
  }
}
