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
public class UpdateClaimStatusConfig {

  @Value("${updateClaimStatusQueue}")
  String updateClaimStatusQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue updateClaimStatusQueue() {
    return new Queue(updateClaimStatusQueue, true, false, true);
  }

  @Bean
  Binding updateClaimStatusBinding() {
    return BindingBuilder.bind(updateClaimStatusQueue())
        .to(bipApiExchange)
        .with(updateClaimStatusQueue);
  }
}
