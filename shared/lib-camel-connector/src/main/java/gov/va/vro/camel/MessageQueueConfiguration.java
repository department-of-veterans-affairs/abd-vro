package gov.va.vro.camel;

import gov.va.vro.camel.config.MessageQueueEnvVariables;
import gov.va.vro.camel.config.MessageQueueProperties;
import gov.va.vro.model.xample.CamelConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class MessageQueueConfiguration {
  private final MessageQueueProperties mqProperties;
  private final MessageQueueEnvVariables mqEnvVariables;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    CachingConnectionFactory factory =
        new CachingConnectionFactory(mqEnvVariables.getHost(), mqEnvVariables.getPort());

    // Prefer environment variables over properties in application.yml
    factory.setHost(mqEnvVariables.getHost());
    factory.setPort(mqEnvVariables.getPort());
    factory.setUsername(mqEnvVariables.getUsername());
    factory.setPassword(mqEnvVariables.getPassword());

    if (factory.getHost() == null) factory.setHost(mqProperties.getHost());
    if (factory.getPort() == 0) factory.setPort(mqProperties.getPort());
    if (factory.getUsername() == null) factory.setUsername(mqProperties.getUsername());

    log.info(
        "rabbitmq ConnectionFactory: connecting to {}:{}", factory.getHost(), factory.getPort());
    return factory;
  }

  /** Required for executing adminstration functions against an AMQP Broker */
  @Bean
  AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(rabbitmqConnectionFactory());
  }

  @Bean
  TopicExchange topicExchangeV3() {
    return new TopicExchange(CamelConstants.V3_EXCHANGE, false, true);
  }

  @Bean
  Queue queuePostResource() {
    return new Queue(CamelConstants.POST_RESOURCE_QUEUE, false, false, true);
  }

  @Bean
  Queue queueGetResource() {
    return new Queue(CamelConstants.GET_RESOURCE_QUEUE, false, false, true);
  }

  @Bean
  Binding bindingExchangeV3WithQueuePostResource() {
    return BindingBuilder.bind(queuePostResource())
        .to(topicExchangeV3())
        .with(CamelConstants.POST_RESOURCE_QUEUE);
  }

  @Bean
  Binding bindingExchangeV3WithQueueGetResource() {
    return BindingBuilder.bind(queueGetResource())
        .to(topicExchangeV3())
        .with(CamelConstants.GET_RESOURCE_QUEUE);
  }
}
