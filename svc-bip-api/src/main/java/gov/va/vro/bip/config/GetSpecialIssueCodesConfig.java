package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetSpecialIssueCodesConfig {

  final String queue;
  final DirectExchange bipApiExchange;
  final RabbitMqConfigProperties props;

  public GetSpecialIssueCodesConfig(
      @Value("${getSpecialIssueTypesQueue}") final String queue,
      final DirectExchange bipApiExchange,
      final RabbitMqConfigProperties props) {
    this.queue = queue;
    this.bipApiExchange = bipApiExchange;
    this.props = props;
  }

  @Bean
  Queue getSpecialIssueTypesQueue() {
    return new Queue(queue, true, false, true, props.getDeadLetterQueueArgs());
  }

  @Bean
  Binding getSpecialIssueTypesBinding() {
    return BindingBuilder.bind(getSpecialIssueTypesQueue()).to(bipApiExchange).with(queue);
  }
}
