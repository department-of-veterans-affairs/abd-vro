package gov.va.vro.camel;

import static java.util.Map.entry;

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

import java.util.Collections;
import java.util.Map;

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
  private final String QUEUE_MESSAGES_DLQ = "bie-events-dlq";
  private final String DLX_EXCHANGE_MESSAGES = "bie-events.dlx";
  Map<String, Object> DLQ_ARGS =
      Map.ofEntries(
          entry("x-dead-letter-exchange", ""),
          entry("x-dead-letter-routing-key", QUEUE_MESSAGES_DLQ));

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
  FanoutExchange deadLetterExchange() {
    return new FanoutExchange(DLX_EXCHANGE_MESSAGES);
  }

  @Bean
  Queue queuePostResource() {
    return new Queue(CamelConstants.POST_RESOURCE_QUEUE, true, false, true);
  }

  @Bean
  Queue queueGetResource() {
    return new Queue(CamelConstants.GET_RESOURCE_QUEUE, true, false, true);
  }

  @Bean
  Queue queueSaveToDbBieAssociated() {
    return new Queue(
        SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_ASSOCIATED, true, false, false, DLQ_ARGS);
  }

  @Bean
  Queue queueSaveToDbBieUpdated() {
    return new Queue(
        SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_UPDATED, true, false, false, DLQ_ARGS);
  }

  @Bean
  Queue queueSaveToDbBieClassified() {
    return new Queue(
        SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_CLASSIFIED, true, false, false, DLQ_ARGS);
  }

  @Bean
  Queue queueSaveToDbBieCompleted() {
    return new Queue(
        SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_COMPLETED, true, false, false, DLQ_ARGS);
  }

  @Bean
  Queue queueSaveToDbBieDeleted() {
    return new Queue(
        SAVE_TO_DB_PREFIX + BIE_EVENTS_CONTENTION_DELETED, true, false, false, DLQ_ARGS);
  }

  @Bean
  Queue deadLetterQueue() {
    return QueueBuilder.durable(QUEUE_MESSAGES_DLQ).build();
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

  @Bean
  Binding deadLetterBinding() {
    return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
  }
}
