package gov.va.vro.routes.xample;

import gov.va.vro.camel.OnExceptionHelper;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.model.biekafka.BieMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
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
      configureRouteToSaveContentionEventToDbFromQueue(queue);
    }
  }

  void configureRouteToSaveContentionEventToDbFromQueue(final String queue) {
    final String exchangeName = queue;
    final String routingKey = queue;
    RabbitMqCamelUtils.fromRabbitmqFanoutExchange(this, exchangeName, routingKey)
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
        .log("Saved Contention Event to DB  ${exchange.pattern}: body ${body.getClass()}")
        .marshal()
        .json(JsonLibrary.Jackson)
        .log("ReceivedMessageEventBody: ${body}");
  }

  void configureExceptionHandling() {
    BiFunction<Exchange, Throwable, BieMessagePayload> exceptionHandler =
        (exchange, cause) -> {
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
