package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CancelClaimConfig {

  final String cancelClaimQueue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public CancelClaimConfig(
      @Value("${cancelClaimQueue}") final String cancelClaimQueue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.cancelClaimQueue = cancelClaimQueue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue cancelClaimQueue() {
    return new Queue(cancelClaimQueue, true, false, true, props.getDeadLetterQueueArgs());
  }

  @Bean
  Binding cancelClaimBinding() {
    return BindingBuilder.bind(cancelClaimQueue()).to(bipApiExchange).with(cancelClaimQueue);
  }
}
