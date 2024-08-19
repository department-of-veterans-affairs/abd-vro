package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetClaimDetailsConfig {

  final String getClaimDetailsQueue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public GetClaimDetailsConfig(
      @Value("${getClaimDetailsQueue}") final String getClaimDetailsQueue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.getClaimDetailsQueue = getClaimDetailsQueue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue getClaimDetailsQueue() {
    return new Queue(getClaimDetailsQueue, true, false, true, props.getGlobalQueueArgs());
  }

  @Bean
  Binding getClaimDetailsBinding() {
    return BindingBuilder.bind(getClaimDetailsQueue())
        .to(bipApiExchange)
        .with(getClaimDetailsQueue);
  }
}
