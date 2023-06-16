package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
  @Value("${abd-data-access.rabbitmq.claim-submit-exchange}")
  private String claimSubmitExchangeName;

  @Value("${abd-data-access.rabbitmq.claim-submit-queue}")
  private String claimSubmitQueueName;

  @Value("${abd-data-access.rabbitmq.claim-submit-routing-key}")
  private String claimSubmitRoutingKey;

  @Bean
  Queue claimSubmitQueue() {
    return new Queue(claimSubmitQueueName, true, false, true);
  }

  @Bean
  DirectExchange claimSubmitExchange() {
    return new DirectExchange(claimSubmitExchangeName, true, true);
  }

  @Bean
  Binding claimSubmitBinding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(claimSubmitRoutingKey);
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
