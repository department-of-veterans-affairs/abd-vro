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
public class ConfigForGetClaimDetails {

  @Value("${getClaimDetailsQueue}")
  String getClaimDetailsQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue getClaimDetailsQueue() {
    return new Queue(getClaimDetailsQueue, true, false, true);
  }

  @Bean
  Binding getClaimDetailsBinding() {
    return BindingBuilder.bind(getClaimDetailsQueue())
        .to(bipApiExchange)
        .with(getClaimDetailsQueue);
  }
}
