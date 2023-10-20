package gov.va.vro.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class RabbitMqCamelUtils {
  public static String wiretapProducer(RouteBuilder builder, String tapName) {
    var exchangeName = "tap-" + tapName;
    var directEndpoint = getTapProducerDirectEndpoint(tapName);
    // Always use ToRabbitMqRouteHelper to create a route to send to RabbitMQ
    new ToRabbitMqRouteHelper(builder, directEndpoint)
        .routeId("to-rabbitmq-" + exchangeName + "-route")
        .toTopic(exchangeName, "tap-" + tapName + "-not-used", "")
        .createRoute();
    return directEndpoint;
  }

  public static String getTapProducerDirectEndpoint(String exchangeName) {
    return "direct:toMqTopic-tap-" + exchangeName;
  }

  public static String wiretapConsumer(String queuePrefix, String tapBasename) {
    return rabbitmqTopicConsumerEndpoint("tap-" + tapBasename, queuePrefix + "-" + tapBasename);
  }

  public static String rabbitmqTopicConsumerEndpoint(String exchangeName, String queueName) {
    return "spring-rabbitmq:" + exchangeName + "?exchangeType=topic&queues=" + queueName;
  }

  public static String rabbitmqFanoutConsumerEndpoint(String exchangeName, String queueName) {
    return "spring-rabbitmq:" + exchangeName + "?exchangeType=fanout&queues=" + queueName;
  }

  public static String rabbitmqConsumerEndpoint(String exchangeName, String routingKey) {
    // Name the queue so that it's easily identifiable; otherwise a random number is set as the
    // queue name
    // At least for Camel, the routingKey parameter is needed to route messages to the queue.
    return "spring-rabbitmq:"
        + exchangeName
        + "?routingKey="
        + routingKey
        + "&queues="
        + routingKey;
  }

  public static RouteDefinition fromRabbitmq(
      RouteBuilder builder, String exchangeName, String routingKey) {
    return fromRabbitmq(builder, rabbitmqConsumerEndpoint(exchangeName, routingKey));
  }

  public static RouteDefinition fromRabbitmqFanoutExchange(
      RouteBuilder builder, String fanoutExchange, String queueName) {
    return fromRabbitmq(builder, rabbitmqFanoutConsumerEndpoint(fanoutExchange, queueName));
  }

  public static RouteDefinition fromRabbitmq(RouteBuilder builder, String rabbitMqUri) {
    if (!rabbitMqUri.startsWith("spring-rabbitmq:"))
      throw new IllegalArgumentException("Endpoint URI must be for RabbitMQ: " + rabbitMqUri);
    return builder.from(rabbitMqUri);
  }
}
