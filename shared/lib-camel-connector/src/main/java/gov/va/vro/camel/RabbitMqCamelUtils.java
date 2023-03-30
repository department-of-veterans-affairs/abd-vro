package gov.va.vro.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class RabbitMqCamelUtils {
  public static String wiretapProducer(String tapName) {
    return rabbitmqTopicProducerEndpoint("tap-" + tapName, "tap-" + tapName + "-not-used");
  }

  static String rabbitmqTopicProducerEndpoint(String exchangeName, String queueName) {
    // Using skipQueueDeclare=true option causes exception, so use skipQueueBind=true instead.
    // Create the queue but don't bind it to the exchange so that messages don't accumulate.
    return "rabbitmq:" + exchangeName + "?skipQueueBind=true&exchangeType=topic&queue=" + queueName;
  }

  public static String wiretapConsumer(String queuePrefix, String tapBasename) {
    return rabbitmqTopicConsumerEndpoint("tap-" + tapBasename, queuePrefix + "-" + tapBasename);
  }

  public static String rabbitmqTopicConsumerEndpoint(String exchangeName, String queueName) {
    return "rabbitmq:" + exchangeName + "?exchangeType=topic&queue=" + queueName;
  }

  public static String rabbitmqProducerEndpoint(String exchangeName, String routingKey) {
    // skipQueueBind=true prevents a randomly-named, unnecessary queue from being generated
    return "rabbitmq:" + exchangeName + "?skipQueueBind=true&routingKey=" + routingKey;
  }

  public static String rabbitmqConsumerEndpoint(String exchangeName, String routingKey) {
    // Name the queue so that it's easily identifiable; otherwise a random number is set as the
    // queue name
    // At least for Camel, the routingKey parameter is needed to route messages to the queue.
    return "rabbitmq:" + exchangeName + "?routingKey=" + routingKey + "&queue=" + routingKey;
  }

  public static RouteDefinition fromRabbitmq(
      RouteBuilder builder, String exchangeName, String routingKey) {
    return fromRabbitmq(builder, rabbitmqConsumerEndpoint(exchangeName, routingKey));
  }

  public static RouteDefinition fromRabbitmq(RouteBuilder builder, String rabbitMqUri) {
    if (!rabbitMqUri.startsWith("rabbitmq:"))
      throw new IllegalArgumentException("Endpoint URI must be for RabbitMQ: " + rabbitMqUri);
    return builder.from(rabbitMqUri);
  }

  public static RouteDefinition addToRabbitmqRoute(
      RouteBuilder builder, String fromUri, String exchangeName, String routingKey) {
    return addToRabbitmqRoute(builder, fromUri, exchangeName, routingKey, "");
  }

  public static RouteDefinition addToRabbitmqRoute(
      RouteBuilder builder,
      String fromUri,
      String exchangeName,
      String routingKey,
      String rabbitmqParams) {
    return addToRabbitmqRoute(builder, fromUri, exchangeName, routingKey, rabbitmqParams, null);
  }

  public static RouteDefinition addToRabbitmqRoute(
      RouteBuilder builder,
      String fromUri,
      String exchangeName,
      String routingKey,
      String rabbitmqParams,
      Class responseClass) {
    var route =
        builder
            .from(fromUri)
            // Remove the CamelRabbitmqExchangeName and CamelRabbitmqRoutingKey so it
            // doesn't interfere with subsequent sending to rabbitmq endpoints
            // https://camel.apache.org/components/3.19.x/rabbitmq-component.html#_troubleshooting_headers:
            // > if the source queue has a routing key set in the headers, it will pass down to
            // > the destination and not be overriden with the URI query parameters.
            .removeHeaders("CamelRabbitmq*")
            .to(rabbitmqProducerEndpoint(exchangeName, routingKey) + rabbitmqParams)
            .id("to-rabbitmq-" + exchangeName + "-" + routingKey);

    if (responseClass != null) route.convertBodyTo(responseClass);

    return route;
  }
}
