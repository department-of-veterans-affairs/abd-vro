package gov.va.vro.services.bie;

import gov.va.vro.services.bie.config.BieProperties;
import lombok.val;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class IntegrationTestConfig {

  @Bean
  Queue bieEventQueue() {
    return new Queue("bieKafkaEvents");
  }

  @Autowired private BieProperties bieProperties;

  @Bean
  String kafkaTopic() {
    // Pick a random kafka topic
    val topics = bieProperties.getKafkaTopicToAmqpExchangeMap().keySet().stream().toList();
    return topics.get(new Random().nextInt(topics.size()));
  }

  @Bean
  public NewTopic newKafkaTopic() {
    return new NewTopic(kafkaTopic(), 1, (short) 1);
  }

  String mqExchangeName() {
    return bieProperties.getKafkaTopicToAmqpExchangeMap().get(kafkaTopic());
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
