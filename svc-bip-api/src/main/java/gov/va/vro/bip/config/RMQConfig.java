package gov.va.vro.bip.config;

import gov.va.vro.bip.model.HasStatusCodeAndMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@Slf4j
public class RMQConfig {

  @Value("${exchangeName}")
  String exchangeName;

  @Bean
  public MessageConverter jackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  DirectExchange bipApiExchange() {
    return new DirectExchange(exchangeName, true, true);
  }

  @Bean
  RabbitListenerErrorHandler svcBipApiErrorHandler() {
    RabbitListenerErrorHandler handler =
        (amqpMessage, message, exception) -> {
          log.info("Oh no!", exception);

          if (message != null && message.getHeaders().getReplyChannel() != null) {
            var errorModel =
                HasStatusCodeAndMessage.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .statusMessage(exception.toString());
            return errorModel;
          }

          return null;
        };
    return handler;
  }
}
