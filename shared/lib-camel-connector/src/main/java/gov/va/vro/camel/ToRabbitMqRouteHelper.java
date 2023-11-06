package gov.va.vro.camel;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

@Setter
public class ToRabbitMqRouteHelper {
  final RouteBuilder routeBuilder;
  final String fromUri;

  @Accessors(fluent = true)
  String routeId = null;

  String rabbitmqProducerUri;

  @Accessors(fluent = true)
  String rabbitMqEndpointId;

  // Setting this converts the JSON string returned from RabbitMQ into the responseClass
  @Accessors(fluent = true)
  Class responseClass = null;

  public ToRabbitMqRouteHelper(RouteBuilder routeBuilder, String fromUri) {
    this.routeBuilder = routeBuilder;
    this.fromUri = fromUri;
  }

  public ToRabbitMqRouteHelper toMq(String exchangeName, String routingKey) {
    return toMq(exchangeName, routingKey, "");
  }

  public ToRabbitMqRouteHelper toMq(String exchangeName, String routingKey, String uriParams) {
    rabbitmqProducerUri = rabbitmqProducerEndpoint(exchangeName, routingKey) + uriParams;
    setDefaultIds(exchangeName, routingKey);
    return this;
  }

  public ToRabbitMqRouteHelper toTopic(String exchangeName, String queueName, String uriParams) {
    // Using skipQueueDeclare=true option causes exception, so use skipQueueBind=true instead.
    // Create the queue but don't bind it to the exchange so that messages don't accumulate.
    rabbitmqProducerUri = rabbitmqTopicProducerEndpoint(exchangeName, queueName) + uriParams;
    setDefaultIds(exchangeName, queueName);
    return this;
  }

  private void setDefaultIds(String exchangeName, String specificName) {
    if (rabbitMqEndpointId == null)
      rabbitMqEndpointId = "to-rabbitmq-" + exchangeName + "-" + specificName;
    if (routeId == null) routeId = "to-rabbitmq-" + exchangeName + "-" + specificName + "-route";
  }

  static String rabbitmqTopicProducerEndpoint(String exchangeName, String queueName) {
    // Using skipQueueDeclare=true option causes exception, so use skipQueueBind=true instead.
    // Create the queue but don't bind it to the exchange so that messages don't accumulate.
    return "spring-rabbitmq:" + exchangeName + "?exchangeType=topic&queues=" + queueName;
  }

  public static String rabbitmqProducerEndpoint(String exchangeName, String routingKey) {
    // skipQueueBind=true prevents a randomly-named, unnecessary queue from being generated
    return "spring-rabbitmq:" + exchangeName + "?routingKey=" + routingKey;
  }

  public RouteDefinition createRoute() {
    var route =
        routeBuilder
            .from(fromUri)
            .routeId(routeId)
            // Remove the CamelRabbitmqExchangeName and CamelRabbitmqRoutingKey so that they
            // don't interfere with subsequent sending to rabbitmq endpoints:
            // https://camel.apache.org/components/3.19.x/rabbitmq-component.html#_troubleshooting_headers:
            // > if the source queue has a routing key set in the headers, it will pass down to
            // > the destination and not be overriden with the URI query parameters.
            .removeHeaders("CamelRabbitmq*")
            .to(rabbitmqProducerUri)
            .id(rabbitMqEndpointId);

    if (responseClass != null) route.convertBodyTo(responseClass);

    return route;
  }
}
