package gov.va.vro.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class RabbitMqCamelUtils {
  public static String wiretapProducer(String tapName) {
    return ToRabbitMqRouteHelper.rabbitmqTopicProducerEndpoint(
        "tap-" + tapName, "tap-" + tapName + "-not-used");
  }

  public static String wiretapConsumer(String queuePrefix, String tapBasename) {
    return rabbitmqTopicConsumerEndpoint("tap-" + tapBasename, queuePrefix + "-" + tapBasename);
  }

  public static String rabbitmqTopicConsumerEndpoint(String exchangeName, String queueName) {
    return "rabbitmq:" + exchangeName + "?exchangeType=topic&queue=" + queueName;
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
}
