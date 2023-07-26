package gov.va.vro.services.bie;

import static gov.va.vro.services.bie.BieKafkaApplicationTest.MQ_EXCHANGE;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfig {

  static final String MQ_QUEUE = "incomingKafkaEvents";

  @Bean
  Queue queue1() {
    return new Queue(MQ_QUEUE, true, false, true);
  }

  @Bean
  FanoutExchange exchange1() {
    return new FanoutExchange(MQ_EXCHANGE, true, true);
  }

  @Bean
  Binding binding1() {
    return BindingBuilder.bind(queue1()).to(exchange1());
  }
}
