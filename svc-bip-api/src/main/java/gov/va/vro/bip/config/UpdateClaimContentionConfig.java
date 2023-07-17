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
public class UpdateClaimContentionConfig {

  @Value("${updateClaimContentionQueue}")
  String updateClaimContentionQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue updateClaimContentionQueue() {
    return new Queue(updateClaimContentionQueue, true, false, true);
  }

  @Bean
  Binding updateClaimContentionBinding() {
    return BindingBuilder.bind(updateClaimContentionQueue())
        .to(bipApiExchange)
        .with(updateClaimContentionQueue);
  }
}
