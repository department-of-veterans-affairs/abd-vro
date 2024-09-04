package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateClaimStatusConfig {

  final String putClaimLifecycleStatusQueue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public UpdateClaimStatusConfig(
      @Value("${putClaimLifecycleStatusQueue}") final String putClaimLifecycleStatusQueue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.putClaimLifecycleStatusQueue = putClaimLifecycleStatusQueue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue putClaimLifecycleStatusQueue() {
    return new Queue(putClaimLifecycleStatusQueue, true, false, true, props.getGlobalQueueArgs());
  }

  @Bean
  Binding updateClaimStatusBinding() {
    return BindingBuilder.bind(putClaimLifecycleStatusQueue())
        .to(bipApiExchange)
        .with(putClaimLifecycleStatusQueue);
  }
}
