package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetClaimContentionsConfig {

  final String getClaimContentionsQueue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public GetClaimContentionsConfig(
      @Value("${getClaimContentionsQueue}") final String getClaimContentionsQueue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.getClaimContentionsQueue = getClaimContentionsQueue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue getClaimContentionsQueue() {
    return new Queue(getClaimContentionsQueue, true, false, true, props.getDeadLetterQueueArgs());
  }

  @Bean
  Binding getClaimContentionsBinding() {
    return BindingBuilder.bind(getClaimContentionsQueue())
        .to(bipApiExchange)
        .with(getClaimContentionsQueue);
  }
}
