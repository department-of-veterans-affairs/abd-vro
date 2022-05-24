package gov.va.vro.routes;

import static gov.va.vro.AppConfig.SEDA_ASYNC_OPTION;

import gov.va.vro.model.Claim;
import gov.va.vro.model.Payload;
import gov.va.vro.services.ClaimProcessorA;
import gov.va.vro.services.ClaimService;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class RrdApiRoute extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    System.out.println(getClass() + ": " + restConfiguration());

    from("seda:logToFile").marshal().json().log(">>2> ${body.getClass()}").to("file://target/post");
    from("seda:addClaim").log(">>3> ${body.getClass()}").bean(ClaimService.class, "addClaim");

    // Could be replaced with
    // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-AddingREST
    rest("/claims")
        // Declares API expectations in results of /api-doc endpoint
        .consumes(MediaType.APPLICATION_JSON)
        .produces(MediaType.APPLICATION_JSON)

        // POST
        .post("/")
        .description("Add claim")
        .type(Claim.class)
        .outType(Claim.class)
        .route()
        .routeId("rest-POST-claim")
        .tracing()
        .log(">>1> ${body.getClass()}")
        .to("seda:addClaim") // save Claim to DB and assign UUID before anything else
        .recipientList(
            constant(
                "seda:logToFile?" + SEDA_ASYNC_OPTION + ",seda:claim-router?" + SEDA_ASYNC_OPTION))
        .parallelProcessing()
        .log(">>5> ${body.toString()}")
        .endRest()

        // GET
        .get("/")
        .description("Get all claims")
        .outType(Claim[].class)
        .route()
        .routeId("claims-getAll")
        .bean(ClaimService.class, "getAllClaims")
        .endRest()

        // GET
        .get("/{id}")
        .description("Get claim")
        .outType(Claim.class)
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
        .outType(Claim.class)
        .route()
        .routeId("claimDetails-getById")
        .bean(ClaimService.class, "claimDetail")
        .endRest()

        // GET
        .get("/{id}/status-diff-from/{status}")
        .description("Returns the claim when it changes from specified 'status'")
        .outType(Claim.class)
        .route()
        .routeId("claim-status-change")
        .setBody(simple("${header.id}"))
        // subscribe on queue, waiting for specified claim to complete
        .pollEnrich(
            simple("seda:claim-rrd-processed-${header.id}?multipleConsumers=true"),
            -1,
            new ChooseSecondExchangeStrategy(),
            false)
        // This works too:
        //                    .process(exchange -> {
        //                        String headerId=exchange.getMessage().getHeader("id",
        // String.class);
        //                        Endpoint endpoint =
        // exchange.getContext().getEndpoint("seda:claim-rrd-processed?multipleConsumers=true");
        //                        PollingConsumer consumer = endpoint.createPollingConsumer();
        //                        Payload body=null;
        //                        do {
        //                            Exchange existingExchange = consumer.receive();
        //                            body = existingExchange.getMessage().getBody(Payload.class);
        //                            System.out.println("submission_id is
        // "+body.getSubmission_id());
        //                        } while(!headerId.equals(body.getSubmission_id()));
        //                        exchange.getMessage().setBody(body);
        //                    })
        .log(">>5> diff status?: ${body}")
        .convertBodyTo(Payload.class)
        .endRest();

    from("seda:claim-rrd-processed?multipleConsumers=true")
        .log(">>>>>>>>> RRD processed! claim: ${body.toString()}");

    rest("/claim")
        // Declares API expectations in results of /api-doc endpoint
        .consumes("application/json")
        .produces("application/json")

        // POST
        .post("processA/")
        .description("Process claim type A")
        .type(Claim.class)
        .outType(Payload.class)
        .to("direct:claimTypeA")

        // testing with GET
        .get("processA/")
        .description("Trigger claim type A")
        .outType(Payload.class)
        //                .to("direct:claimTypeA")
        .route()
        .routeId("inject-claim")
        .bean(ClaimProcessorA.class, "claimFactory")
        .to("direct:claimTypeA")
        .endRest();
  }

  public class ChooseSecondExchangeStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
      System.out.println("------------- ChooseSecondExchangeStrategy");
      if (newExchange == null) {
        return oldExchange;
      } else {
        return newExchange;
      }
      //            Object oldBody = oldExchange.getIn().getBody();
      //            Object newBody = newExchange.getIn().getBody();
      //            oldExchange.getIn().setBody(oldBody + ":" + newBody);
      //            return oldExchange;
    }
  }
}
