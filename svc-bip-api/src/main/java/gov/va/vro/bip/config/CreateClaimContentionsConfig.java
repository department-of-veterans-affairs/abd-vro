package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CreateClaimContentionsConfig {

  @Value("${createClaimContentionsQueue}")
  String createClaimContentionsQueue;

  @Autowired DirectExchange bipApiExchange;
  @Autowired RabbitMqConfigProperties props;

  @Bean
  Queue createClaimContentionsQueue() {
    return new Queue(createClaimContentionsQueue, true, false, true, props.getDeadLetterQueueArgs());
  }

  @Bean
  Binding createClaimContentionsBinding() {
    return BindingBuilder.bind(createClaimContentionsQueue())
        .to(bipApiExchange)
        .with(createClaimContentionsQueue);
  }
}
