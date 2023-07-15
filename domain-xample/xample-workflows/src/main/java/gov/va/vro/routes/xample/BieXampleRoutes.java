package gov.va.vro.routes.xample;

import gov.va.vro.biekafka.model.BieMessagePayload;
import gov.va.vro.camel.OnExceptionHelper;
import gov.va.vro.camel.RabbitMqCamelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

// https://camel.apache.org/manual/Endpoint-dsl.html
@Slf4j
@Component
@RequiredArgsConstructor
public class BieXampleRoutes extends EndpointRouteBuilder {

  @Autowired final DbHelper dbHelper;

  @Value("#{'${bie.queues}'.split(' ')}")
  private final List<String> queues;

  @Override
  public void configure() {
    configureExceptionHandling();
    for (String queue : queues) {
      configureSaveContentionEventToDbFromQueue(queue);
    }
  }

  void configureSaveContentionEventToDbFromQueue(final String queue) {
    // The exchange and queue share the same name
    RabbitMqCamelUtils.fromRabbitmqFanoutExchange(this, queue, queue)
        .routeId(queue + "-saveToDb-route")
        .log("Received ${headers} ${body.getClass()}: ${body}")
        .convertBodyTo(BieMessagePayload.class)
        .log("Converted to ${body.getClass()}: ${body}")
        .log("Saving Contention Event to DB")
        .process(
            exchange -> {
              final BieMessagePayload body = exchange.getMessage().getBody(BieMessagePayload.class);
              dbHelper.saveContentionEvent(body);
              body.setStatus(200);
              exchange.getMessage().setBody(body);
            })
        .log("Saved Contention Event to DB  ${exchange.pattern}: body ${body.getClass()}: ${body}");
  }

  void configureExceptionHandling() {
    BiFunction<Exchange, Throwable, BieMessagePayload> exceptionHandler =
        (exchange, cause) -> {
          // TODO: replace with biemessagepayload class
          //          var clazz = exchange.getMessage().getHeader()
          var body = exchange.getMessage().getBody(BieMessagePayload.class);
          body.setStatus(500);
          body.setStatusMessage(cause.toString());
          exchange.getMessage().setBody(body);
          return body;
        };

    OnExceptionHelper.catchExceptionsFor(this, Throwable.class, true, exceptionHandler)
        .log("Exception occurred ${exchange.pattern}: Returning ${body.getClass()}: ${body}");
  }
}
