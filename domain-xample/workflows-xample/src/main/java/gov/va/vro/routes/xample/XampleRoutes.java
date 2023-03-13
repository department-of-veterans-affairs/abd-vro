package gov.va.vro.routes.xample;

import static gov.va.vro.model.xample.CamelConstants.POST_RESOURCE_QUEUE;
import static gov.va.vro.model.xample.CamelConstants.V3_EXCHANGE;

import gov.va.vro.camel.OnExceptionHelper;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.services.xample.ServiceB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

// https://camel.apache.org/manual/Endpoint-dsl.html
@Slf4j
@Component
@RequiredArgsConstructor
public class XampleRoutes extends EndpointRouteBuilder {

  @Autowired final DbHelper dbHelper;

  @Override
  public void configure() {
    configureExceptionHandling();
    configurePostResourceRoute();
    configureServicesRoute();

    configureFetchResourceRoute();
  }

  // Camel endpoint to do background processing asynchronously using the fancy service.
  // Set ExchangePattern.InOnly in case a different ExchangePattern is set in the route.
  // https://camel.apache.org/manual/exchange-pattern.html
  static final String SEDA_SERVICE_ENDPOINT = "seda:fancy-service?exchangePattern=InOnly";

  void configurePostResourceRoute() {
    from("direct:saveToDb")
        .process(
            new FunctionProcessor<SomeDtoModel, ClaimEntity>(
                SomeDtoModel.class, model -> dbHelper.saveToDb(model)));

    var setStatusReceived =
        new FunctionProcessor<SomeDtoModel, SomeDtoModel>(
            SomeDtoModel.class, model -> model.status(StatusValue.RECEIVED));

    // This route looks long due to the code comments but it's not doing much:
    // save the incoming body to the DB and then send it to be processed in another route.
    // Keep routes straightforward; decompose a long route into several smaller routes,
    // which also facilitate testing.
    RabbitMqCamelUtils.fromRabbitmq(this, V3_EXCHANGE, POST_RESOURCE_QUEUE)
        // Set the route's id so that log messages for this route are easily identified
        .routeId("postXResource-route")

        // The payload was automatically marshalled by CamelDtoConverter into a JSON String so that
        // it can be transmitted through RabbitMQ.
        // The log message shows "[B" (array of bytes), a.k.a. a String.
        .log("Received ${headers} ${body.getClass()}: ${body}")
        // Explicitly unmarshal the JSON String into an object, which can be passed around and
        // modified. If this was omitted, the msg body would stay a JSON String and would be
        // automatically converted to the expected input object type for each processor, which
        // is inefficient.
        .convertBodyTo(SomeDtoModel.class)
        .log("Converted to ${body.getClass()}: ${body}")

        // Update the status and pass on the modified body
        .process(setStatusReceived)

        // We want to ensure saveToDB completes before continuing to next processor but we don't
        // want pass saveToDb's returned object to the next processor; we want to pass the current
        // SomeDtoModel body instead. To do this, we set InOnly so that the incoming message
        // body is passed on to the next processor after saveToDb is complete.
        .to(ExchangePattern.InOnly, "direct:saveToDb")
        .log("After saveToDb, ${exchange.pattern}: body ${body.getClass()}: ${body}")

        // Use seda to asynchronously route message to a service for background processing.
        // Since SEDA_SERVICE_ENDPOINT is specifically configured for `exchangePattern=InOnly`,
        // the current ExchangePattern (InOut) is irrelevant for this `.to()` call.
        // We could have used `.to(ExchangePattern.InOnly,SEDA_SERVICE_ENDPOINT)` instead.
        .to(SEDA_SERVICE_ENDPOINT)
        // Simultaneously, continue with the route (update the 'status' field) and ultimately
        // return the body back to the Controller.
        // .delay(2000)
        .process(
            // Demonstrating an alternative to creating a FunctionProcessor like setStatusReceived
            exchange -> {
              var body = exchange.getMessage().getBody(SomeDtoModel.class);
              body.status(StatusValue.PROCESSING);
              // Gotcha: Always set the output body in case the input body is an (immutable)
              // object like a String. If `setBody()` is not called, the input body is passed
              // as the output message body, which can work for mutable objects but
              // we shouldn't rely on it because `getBody()` can automatically convert
              // a JSON string into a mutable object.
              exchange.getMessage().setBody(body);
            })
        // Expect this log message before final log from the longer-running SEDA_SERVICE_ENDPOINT
        .log("${exchange.pattern}: ${headers} Returning to caller: ${body.class}");
  }

  void configureServicesRoute() {
    configureRouteToServiceA();
    configureRouteToServiceJ();

    // Route to particular service depending on message body
    // Demonstrates different ways to trigger a service
    from(SEDA_SERVICE_ENDPOINT)
        .routeId("fancyService-route")
        .log("${exchange.pattern}: ${headers} Sending to background service: ${body.class}")
        // Set InOut to wait for response from service
        .setExchangePattern(ExchangePattern.InOut)
        .choice() // begin ChoiceDefinition
        //
        .when(simple("${body.diagnosticCode} == 'A'"))
        .to(SERVICE_A_ENDPOINT)
        //
        .when(simple("${body.diagnosticCode} == 'B'"))
        // https://camel.apache.org/components/3.19.x/eips/bean-eip.html
        .bean(ServiceB.class)
        //
        .when(simple("${body.diagnosticCode} == 'J'"))
        .to(SERVICE_J_ENDPOINT)
        // Set an id for this route node so tests can replace this rabbitmq endpoint with a mock
        .id("serviceJ-via-rabbitmq")
        //
        .otherwise()
        .to("direct:no-where")
        //
        .end() // end ChoiceDefinition
        .log("Response from service: ${body.getClass()}: ${body}");
  }

  final String SERVICE_A_ENDPOINT = "direct:serviceA";
  final String SERVICE_J_ENDPOINT = "direct:serviceJ";

  void configureRouteToServiceA() {
    var setStatusDone =
        new FunctionProcessor<SomeDtoModel, SomeDtoModel>(
            SomeDtoModel.class, model -> model.status(StatusValue.DONE));

    from(SERVICE_A_ENDPOINT)
        .log("Simulating serviceA processing in the background... delaying")
        .delay(2_000)
        .process(setStatusDone);
  }

  void configureRouteToServiceJ() {
    // Always use this utility method to send to RabbitMQ
    RabbitMqCamelUtils.addToRabbitmqRoute(this, SERVICE_J_ENDPOINT, "xample", "serviceJ")
        .convertBodyTo(SomeDtoModel.class);
  }

  void configureFetchResourceRoute() {
    // TODO: ...
  }

  void configureExceptionHandling() {
    BiFunction<Exchange, Throwable, SomeDtoModel> exceptionHandler =
        (exchange, cause) -> {
          var body = exchange.getMessage().getBody(SomeDtoModel.class);
          // Populate the status and reason for the response
          return body.status(StatusValue.ERROR, cause.toString());
        };

    OnExceptionHelper.catchExceptionsFor(this, Throwable.class, true, exceptionHandler)
        .log("Exception occurred ${exchange.pattern}: Returning ${body.getClass()}: ${body}");
  }
}
