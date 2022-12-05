package gov.va.vro.service.provider.camel;

public class VroCamelUtils {
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
}
