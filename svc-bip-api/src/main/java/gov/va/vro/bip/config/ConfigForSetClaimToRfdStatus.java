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
public class ConfigForSetClaimToRfdStatus {
  @Value("${setClaimToRfdStatusQueue}")
  String setClaimToRfdStatusQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue setClaimToRfdStatusQueue() {
    return new Queue(setClaimToRfdStatusQueue, true, false, true);
  }

  @Bean
  Binding setClaimToRfdStatusBinding() {
    return BindingBuilder.bind(setClaimToRfdStatusQueue())
        .to(bipApiExchange)
        .with(setClaimToRfdStatusQueue);
  }
}
