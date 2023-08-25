package gov.va.vro.services.bie.config;

import gov.va.vro.model.biekafka.ContentionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractDeclarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class MessageExchangeConfig {

  private static final boolean IS_DURABLE = true;
  private static final boolean IS_AUTO_DELETED = true;

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  Declarables topicBindings(final BieProperties bieProperties) {
    final List<AbstractDeclarable> list =
        Arrays.stream(bieProperties.topicNames())
            .map(
                topic -> {
                  final FanoutExchange fanoutExchange =
                      new FanoutExchange(
                          ContentionEvent.rabbitMqExchangeName(topic),
                          IS_DURABLE,
                          IS_AUTO_DELETED);
                  log.info("event=setUpMQ exchange={}", fanoutExchange);
                  return List.of(fanoutExchange);
                })
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return new Declarables(list);
  }
}
