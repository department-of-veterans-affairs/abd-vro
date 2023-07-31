package gov.va.vro.services.bie;

import gov.va.vro.services.bie.config.BieProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Arrays;
import java.util.Random;

@Configuration
@Slf4j
public class IntegrationTestConfig {

  @Autowired private BieProperties bieProperties;

  // ###### Kafka configuration:

  @Autowired KafkaAdmin kafkaAdmin;

  public void createNewKafkaTopics() {
    val newTopics =
        bieProperties.getKafkaTopicToAmqpExchangeMap().keySet().stream()
            .map(topic -> new NewTopic(topic, 1, (short) 1))
            .toArray(NewTopic[]::new);
    log.info("====== Creating Kafka topics: {}", Arrays.toString(newTopics));
    kafkaAdmin.createOrModifyTopics(newTopics);
  }

  @Bean
  String kafkaTopic() {
    //    createNewKafkaTopics();
    // Pick a random kafka topic
    val topics = bieProperties.getKafkaTopicToAmqpExchangeMap().keySet().stream().toList();
    return topics.get(new Random().nextInt(topics.size()));
  }

  // ###### MQ configuration:

  @Bean
  Queue bieEventQueue() {
    return new Queue("bieKafkaEvents");
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
