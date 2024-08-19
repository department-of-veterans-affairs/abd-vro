package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateClaimContentionsConfig {

  final String updateClaimContentionsQueue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public UpdateClaimContentionsConfig(
      @Value("${updateClaimContentionsQueue}") final String updateClaimContentionsQueue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.updateClaimContentionsQueue = updateClaimContentionsQueue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue updateClaimContentionsQueue() {
    return new Queue(updateClaimContentionsQueue, true, false, true, props.getGlobalQueueArgs());
  }

  @Bean
  Binding updateClaimContentionsBinding() {
    return BindingBuilder.bind(updateClaimContentionsQueue())
        .to(bipApiExchange)
        .with(updateClaimContentionsQueue);
  }
}
