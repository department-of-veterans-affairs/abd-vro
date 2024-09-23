package gov.va.vro.services.bie;

import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.services.bie.config.BieKafkaProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@Slf4j
public class IntegrationTestConfig {

  @Autowired private BieKafkaProperties bieKafkaProperties;

  // ###### Kafka configuration:

  @Bean
  String kafkaTopic() {
    // Pick a random kafka topic
    val topics = bieKafkaProperties.topicNames();
    return topics[new Random().nextInt(topics.length)];
  }

  // ###### MQ configuration:

  @Bean
  Queue bieEventQueue() {
    return new Queue("bieKafkaEvents", true, false, true);
  }

  String mqExchangeName() {
    return ContentionEvent.rabbitMqExchangeName(kafkaTopic());
  }

  @Bean
  FanoutExchange fanoutExchange() {
    return new FanoutExchange(mqExchangeName(), true, false);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(bieEventQueue()).to(fanoutExchange());
  }
}
