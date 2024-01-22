package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateClaimContentionsConfig {

  @Value("${updateClaimContentionsQueue}")
  String updateClaimContentionsQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue updateClaimContentionsQueue() {
    return new Queue(updateClaimContentionsQueue, true, false, true);
  }

  @Bean
  Binding updateClaimContentionsBinding() {
    return BindingBuilder.bind(updateClaimContentionsQueue())
        .to(bipApiExchange)
        .with(updateClaimContentionsQueue);
  }
}
