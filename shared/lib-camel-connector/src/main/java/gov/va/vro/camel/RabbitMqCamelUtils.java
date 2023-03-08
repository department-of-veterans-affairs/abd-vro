package gov.va.vro.camel;

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

  static String rabbitmqTopicConsumerEndpoint(String exchangeName, String queueName) {
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
}
