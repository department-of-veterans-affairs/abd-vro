package gov.va.vro.services.bie;

import gov.va.vro.services.bie.config.BieProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@Slf4j
public class IntegrationTestConfig {

  @Autowired private BieProperties bieProperties;

  // ###### Kafka configuration:

  @Bean
  String kafkaTopic() {
    // Pick a random kafka topic
    val topics = bieProperties.topicNames();
    return topics[new Random().nextInt(topics.length)];
  }

  // ###### MQ configuration:

  @Bean
  Queue bieEventQueue() {
    return new Queue("bieKafkaEvents");
  }

  String mqExchangeName() {
    return bieProperties.getTopicToExchangeMap().get(kafkaTopic());
  }

  @Bean
  FanoutExchange fanoutExchange() {
    return new FanoutExchange(mqExchangeName(), true, true);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(bieEventQueue()).to(fanoutExchange());
  }
}
