package gov.va.vro.routes.xample;

import static gov.va.vro.model.xample.CamelConstants.POST_RESOURCE_QUEUE;
import static gov.va.vro.model.xample.CamelConstants.V3_EXCHANGE;

import gov.va.vro.camel.OnExceptionHelper;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import gov.va.vro.persistence.model.ClaimEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    configureMainRoute();
    configureServicesRoute();
  }

  // Camel endpoint to do background processing asynchronously using the fancy service.
  // Set ExchangePattern.InOnly in case a different ExchangePattern is set in the route.
  // https://camel.apache.org/manual/exchange-pattern.html
  final String FANCY_SERVICE_ENDPOINT = "seda:fancy-service?exchangePattern=InOnly";

  void configureMainRoute() {
    var saveToDb =
        new FunctionProcessor<SomeDtoModel, ClaimEntity>(
            SomeDtoModel.class, model -> dbHelper.saveToDb(model));

    var setStatusReceived =
        new FunctionProcessor<SomeDtoModel, SomeDtoModel>(
            SomeDtoModel.class, model -> model.status(StatusValue.RECEIVED));

    RabbitMqCamelUtils.fromRabbitmq(this, V3_EXCHANGE, POST_RESOURCE_QUEUE)
        // Set the route's id so that log messages for this route are easily identified
        .id("postXResource-route")

        // The payload was automatically marshalled by CamelDtoConverter into a JSON String so that
        // it can be transmitted through RabbitMQ.
        // The log message shows "[B" (array of bytes), a.k.a. a String.
        .log("Received ${body.getClass()}: ${body}")
        // Explicitly unmarshal the JSON String into an object, which can be passed around and
        // modified. If this was omitted, the msg body would stay a JSON String and would be
        // automatically converted to the expected input object type for each processor, which
        // is inefficient.
        .convertBodyTo(SomeDtoModel.class)
        .log("Converted to ${body.getClass()}: ${body}")

        // Update the status and pass on the modified body
        .process(setStatusReceived)

        // We want to ensure saveToDB completes before continuing to next processor but don't pass
        // saveToDb's returned object to the next processor. To do this, we set InOnly so that the
        // incoming message body is passed on to the next processor after saveToDb is complete.
        .setExchangePattern(ExchangePattern.InOnly)
        .process(saveToDb)
        .log("After saveToDb, body ${body.getClass()}: ${body}")
        // Restore InOut so that a result can be returned to the route's caller
        .setExchangePattern(ExchangePattern.InOut)

        // Use seda to asynchronously route message to a service for background processing.
        // Since FANCY_SERVICE_ENDPOINT is specifically configured for `exchangePattern=InOnly`,
        // the current ExchangePattern (InOut) is irrelevant for this `.to()` call.
        .to(FANCY_SERVICE_ENDPOINT)
        // Simultaneously, continue with the route (update the 'status' field) and ultimately
        // return the body back to the Controller.
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
        // Expect this log message before the final log message from the long-running FANCY_SERVICE
        .log("${exchange.pattern}: Returning to caller: ${body.class}");
  }

  void configureServicesRoute() {
    final String DIRECT_TO_MQ_XAMPLE_SERVICE_J = "direct:toMq-xample--serviceJ";
    RabbitMqCamelUtils.addToRabbitmqRoute(
        this, DIRECT_TO_MQ_XAMPLE_SERVICE_J, "xample", "serviceJ");

    // Route to particular service depending on message body
    // Demonstrates different ways to trigger a service
    from(FANCY_SERVICE_ENDPOINT)
        .id("fancyService-route")
        .choice() // begin ChoiceDefinition
        .when(simple("${body.diagnosticCode} == 'A'"))
        .to(SERVICE_A_ENDPOINT)
        .when(simple("${body.diagnosticCode} == 'B'"))
        // https://camel.apache.org/components/3.19.x/eips/bean-eip.html
        .bean(ServiceB.class)
        .otherwise()
        .to(DIRECT_TO_MQ_XAMPLE_SERVICE_J)
        .end() // end ChoiceDefinition
        .log("Finally returning from service: ${body.getClass()}: ${body}");

    configureServiceA();
  }

  final String SERVICE_A_ENDPOINT = "direct:serviceA";

  void configureServiceA() {
    var setStatusDone =
        new FunctionProcessor<SomeDtoModel, SomeDtoModel>(
            SomeDtoModel.class, model -> model.status(StatusValue.DONE));

    from(SERVICE_A_ENDPOINT)
        .log("Simulating serviceA processing in the background... delaying")
        .delay(10_000)
        .process(setStatusDone);
  }

  static class ServiceB {
    @SneakyThrows
    public SomeDtoModel processRequest(SomeDtoModel model) {
      log.info("Simulating serviceB processing in the background... delaying");
      Thread.sleep(8_000);

      model.status(StatusValue.DONE);
      return model;
    }
  }

  void configureExceptionHandling() {
    BiFunction<Exchange, Throwable, SomeDtoModel> exceptionHandler =
        (exchange, cause) -> {
          var body = exchange.getMessage().getBody(SomeDtoModel.class);
          // Populate the status and reason for the response
          return body.status(StatusValue.ERROR).reason(cause.toString());
        };

    OnExceptionHelper.catchExceptionsFor(this, Throwable.class, true, exceptionHandler)
        .log("${exchange.pattern}: Returning ${body.getClass()}: ${body}");
  }
}
