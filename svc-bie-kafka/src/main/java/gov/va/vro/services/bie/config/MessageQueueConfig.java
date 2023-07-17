package gov.va.vro.services.bie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractDeclarable;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class MessageQueueConfig {

  private static final boolean DURABLE = true;
  private static final boolean AUTO_DELETE = true;
  private static final boolean EXCLUSIVE = false;

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  Declarables topicBindings(final BieProperties bieProperties) {
    final List<AbstractDeclarable> list =
        bieProperties.getKafkaTopicToAmqpQueueMap().values().stream()
            .map(
                topic -> {
                  final Queue queue = new Queue(topic, DURABLE, EXCLUSIVE, AUTO_DELETE);
                  final FanoutExchange fanoutExchange =
                      new FanoutExchange(topic, DURABLE, AUTO_DELETE);
                  final Binding binding = BindingBuilder.bind(queue).to(fanoutExchange);
                  log.info(
                      "event=setUpMQ queue={} exchange={} binding={}",
                      queue,
                      fanoutExchange,
                      binding);
                  return List.of(queue, fanoutExchange, binding);
                })
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return new Declarables(list);
  }
}
