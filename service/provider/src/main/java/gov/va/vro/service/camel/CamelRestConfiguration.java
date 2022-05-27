package gov.va.vro.service.camel;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.model.Payload;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

/**
 * Not for production. For quick development and testing of an API in dev. Don't have to implement
 * API, controller, and request-response mapper classes.
 */
@Component
@ConditionalOnProperty(
    value = "vro.camel_rest_api.enable",
    havingValue = "true",
    matchIfMissing = false)
class CamelRestConfiguration extends RouteBuilder {

  @Value("${vro.context_path}")
  public String contextPath;

  @Autowired private Environment env;

  @Override
  public void configure() throws Exception {
    // servletName must match servlet given to ServletRegistrationBean in AppConfig.java
    // https://medium.com/@simon.martinelli/apache-camel-rest-endpoint-and-servlet-name-dd827a56716f
    // https://camel.apache.org/components/3.14.x/servlet-component.html#_putting_camel_jars_in_the_app_server_boot_classpath
    restConfiguration()
        .component("servlet")
        .endpointProperty("servletName", CamelConfiguration.servletName)
        .contextPath(contextPath)
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true") // useful for debugging
        // .dataFormatProperty("json.in.disableFeatures",
        // "FAIL_ON_UNKNOWN_PROPERTIES,ADJUST_DATES_TO_CONTEXT_TIME_ZONE")
        // .dataFormatProperty("json.in.enableFeatures",
        // "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
        .apiContextRouteId("doc-api")
        .apiContextPath("/api-doc")
        .apiVendorExtension(true)
        .apiProperty("api.title", "VRO Camel REST API")
        .apiProperty("cors", "true");

    String port = env.getProperty("server.port");
    log.info(
        "=== VRO === Enabled Camel REST API at http://localhost:"
            + port
            + restConfiguration().getContextPath()
            + "  Swagger spec at subpath "
            + restConfiguration().getApiContextPath());
    configureEndpoints();
  }

  public void configureEndpoints() {
    // Could be replaced with
    // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-AddingREST
    rest("/claims")
        // Declares API expectations in results of /api-doc endpoint
        .consumes(MediaType.APPLICATION_JSON)
        .produces(MediaType.APPLICATION_JSON)

        // POST
        .post("/")
        .description("Add claim")
        .type(ClaimSubmission.class)
        .outType(ClaimSubmission.class)
        .route()
        .routeId("rest-POST-claim")
        .tracing()
        .to("direct:postClaim")
        .endRest()

        // GET
        .get("/")
        .description("Get all claims")
        .outType(ClaimSubmission[].class)
        .route()
        .routeId("claims-getAll")
        .bean(CamelClaimService.class, "getAllClaims")
        .endRest()

        // GET
        .get("/{id}")
        .description("Get claim")
        .outType(ClaimSubmission.class)
        .route()
        .routeId("claims-getById")
        // https://camel.apache.org/components/3.14.x/languages/simple-language.html#_variables
        .setBody(simple("${header.id}"))
        .bean(CamelClaimService.class, "getClaim")
        .log(">>3> ${body.toString()}")
        .endRest()

        // GET details
        .get("/details/{id}")
        .description("Get claim")
        .outType(ClaimSubmission.class)
        .route()
        .routeId("claimDetails-getById")
        .bean(CamelClaimService.class, "claimDetail")
        .endRest()

        // GET blocks until claim status changes
        .get("/{id}/status-change")
        .description("Returns the claim when it changes")
        .outType(ClaimSubmission.class)
        .route()
        .routeId("claim-status-change")
        .setBody(simple("${header.id}"))
        // subscribe on queue, waiting for specified claim to complete
        .pollEnrich(
            simple("seda:claim-vro-processed-${header.id}?multipleConsumers=true"),
            -1,
            new ChooseSecondExchangeStrategy(),
            false)
        .log(">>5> ${body}")
        .convertBodyTo(Payload.class)
        .endRest();
  }

  public class ChooseSecondExchangeStrategy implements AggregationStrategy {
    /**
     * @param exchange1 with message body = id
     * @param exchange2 with message body from the seda:claim-vro-processed-${header.id} endpoint
     * @return exchange2
     */
    public Exchange aggregate(Exchange exchange1, Exchange exchange2) {
      Object body2 = exchange2.getIn().getBody();
      log.info("ChooseSecondExchangeStrategy: " + body2);
      if (exchange2 == null) return exchange1;

      return exchange2;
    }
  }
}
