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
public class CancelClaimConfig {

  @Value("${cancelClaimQueue}")
  String cancelClaimQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue cancelClaimQueue() {
    return new Queue(cancelClaimQueue, true, false, true);
  }

  @Bean
  Binding cancelClaimBinding() {
    return BindingBuilder.bind(cancelClaimQueue()).to(bipApiExchange).with(cancelClaimQueue);
  }
}
