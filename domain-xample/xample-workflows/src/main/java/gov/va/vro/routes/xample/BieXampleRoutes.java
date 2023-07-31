package gov.va.vro.routes.xample;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.camel.OnExceptionHelper;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.model.biekafka.BieMessagePayload;
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

  @Value("#{'${bie.exchanges}'.split(' ')}")
  private final List<String> exchanges;

  @Override
  public void configure() {
    configureExceptionHandling();
    for (String exchange : exchanges) {
      configureRouteToSaveContentionEventToDbFromQueue(exchange);
    }
  }

  void configureRouteToSaveContentionEventToDbFromQueue(final String exchangeName) {
    final String queueName = "saveToDB-" + exchangeName;
    RabbitMqCamelUtils.fromRabbitmqFanoutExchange(this, exchangeName, queueName)
        .routeId(exchangeName + "-saveToDb-route")
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
        .process(
            exchange -> {
              final BieMessagePayload body = exchange.getMessage().getBody(BieMessagePayload.class);
              final ObjectMapper objectMapper = new ObjectMapper();
              final String jsonBody = objectMapper.writeValueAsString(body);
              log.info("ReceivedMessageEventBody: " + jsonBody);
            });
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
