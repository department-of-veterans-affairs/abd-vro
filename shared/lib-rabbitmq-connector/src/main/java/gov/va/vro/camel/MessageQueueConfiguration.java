package gov.va.vro.camel;

import gov.va.vro.camel.config.MessageQueueEnvVariables;
import gov.va.vro.camel.config.MessageQueueProperties;
import gov.va.vro.model.xample.RabbitMqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Slf4j
@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class MessageQueueConfiguration {
  private final MessageQueueProperties mqProperties;
  private final MessageQueueEnvVariables mqEnvVariables;
  private final String BIE_EVENTS_CONTENTION_ASSOCIATED = "bie-events-contention-associated";
  private final String BIE_EVENTS_CONTENTION_UPDATED = "bie-events-contention-updated";
  private final String BIE_EVENTS_CONTENTION_CLASSIFIED = "bie-events-contention-classified";
  private final String BIE_EVENTS_CONTENTION_COMPLETED = "bie-events-contention-completed";
  private final String BIE_EVENTS_CONTENTION_DELETED = "bie-events-contention-deleted";
  private final String SAVE_TO_DB_PREFIX = "saveToDB-";

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
  public AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(rabbitmqConnectionFactory());
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public TopicExchange topicExchangeV3() {
    return new TopicExchange(RabbitMqConstants.V3_EXCHANGE, false, true);
  }

  @Bean
  FanoutExchange fanoutExchangeBieEventsContentionAssociated() {
    return new FanoutExchange(
        BIE_EVENTS_CONTENTION_ASSOCIATED,
        true,
        true,
        Collections.singletonMap("queues", queueSaveToDbBieAssociated().getName()));
  }

  @Bean
  FanoutExchange fanoutExchangeBieEventsSaveToDbBieClassified() {
    return new FanoutExchange(
        BIE_EVENTS_CONTENTION_CLASSIFIED,
        true,
        true,
        Collections.singletonMap("queues", queueSaveToDbBieClassified().getName()));
  }

  @Bean
  FanoutExchange fanoutExchangeBieEventsContentionCompleted() {
    return new FanoutExchange(
        BIE_EVENTS_CONTENTION_COMPLETED,
        true,
        true,
        Collections.singletonMap("queues", queueSaveToDbBieCompleted().getName()));
  }

  @Bean
  FanoutExchange fanoutExchangeBieEventsContentionUpdated() {
    return new FanoutExchange(
        BIE_EVENTS_CONTENTION_UPDATED,
        true,
        true,
        Collections.singletonMap("queues", queueSaveToDbBieUpdated().getName()));
  }

  @Bean
  FanoutExchange fanoutExchangeBieEventsContentionDeleted() {
    return new FanoutExchange(
        BIE_EVENTS_CONTENTION_DELETED,
        true,
        true,
        Collections.singletonMap("queues", queueSaveToDbBieDeleted().getName()));
  }

  @Bean
  Queue queuePostResource() {
    return new Queue(RabbitMqConstants.POST_RESOURCE_QUEUE, true, false, true);
  }

  @Bean
  Queue queueGetResource() {
    return new Queue(RabbitMqConstants.GET_RESOURCE_QUEUE, true, false, true);
  }

  @Bean
  Queue queueSaveToDbBieAssociated() {
    return new Queue(SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_ASSOCIATED, true, false, false);
  }

  @Bean
  Queue queueSaveToDbBieUpdated() {
    return new Queue(SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_UPDATED, true, false, false);
  }

  @Bean
  Queue queueSaveToDbBieClassified() {
    return new Queue(SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_CLASSIFIED, true, false, false);
  }

  @Bean
  Queue queueSaveToDbBieCompleted() {
    return new Queue(SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_COMPLETED, true, false, false);
  }

  @Bean
  Queue queueSaveToDbBieDeleted() {
    return new Queue(SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_DELETED, true, false, false);
  }

  @Bean
  Binding bindingExchangeBieDeleted() {
    return BindingBuilder.bind(queueSaveToDbBieDeleted())
        .to(fanoutExchangeBieEventsContentionDeleted());
  }

  @Bean
  Binding bindingExchangeBieUpdated() {
    return BindingBuilder.bind(queueSaveToDbBieUpdated())
        .to(fanoutExchangeBieEventsContentionUpdated());
  }

  @Bean
  Binding bindingExchangeBieCompleted() {
    return BindingBuilder.bind(queueSaveToDbBieCompleted())
        .to(fanoutExchangeBieEventsContentionCompleted());
  }

  @Bean
  Binding bindingExchangeBieClassified() {
    return BindingBuilder.bind(queueSaveToDbBieClassified())
        .to(fanoutExchangeBieEventsSaveToDbBieClassified());
  }

  @Bean
  Binding bindingExchangeBieAssociated() {
    return BindingBuilder.bind(queueSaveToDbBieAssociated())
        .to(fanoutExchangeBieEventsContentionAssociated());
  }
}
