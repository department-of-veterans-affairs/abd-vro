package gov.va.vro.bip.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.bip.model.HasStatusCodeAndMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
public class RabbitMqConfig {

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

  @Bean
  RabbitListenerErrorHandler svcBipApiErrorHandlerV2(ObjectMapper mapper) {
    return (amqpMessage, message, exception) -> {
      if (exception.getCause() instanceof HttpStatusCodeException e) {
        String url =
            Optional.ofNullable(e.getResponseHeaders())
                .map(HttpHeaders::getLocation)
                .map(URI::getPath)
                .orElse("No Headers");
        int status = e.getStatusCode().value();
        String statusMessage = ((HttpStatus) e.getStatusCode()).name();
        log.info(
            "event=responseReceived url={} status={} statusMessage={}", url, status, statusMessage);

        List<BipMessage> messages = new ArrayList<>();
        try {
          messages.addAll(
              mapper
                  .readValue(e.getResponseBodyAsString(), BipPayloadResponse.class)
                  .getMessages());
        } catch (JsonProcessingException ex) {
          log.info(
              "event=failedToParseResponse url={} status={} statusMessage={} error={}",
              url,
              status,
              statusMessage,
              ex.getMessage());
        }
        return BipPayloadResponse.builder()
            .statusCode(status)
            .statusMessage(statusMessage)
            .messages(messages)
            .build();

      } else {
        return BipPayloadResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .statusMessage(HttpStatus.INTERNAL_SERVER_ERROR.name())
            .build();
      }
    };
  }
}
