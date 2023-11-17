package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Component
public class BipRequestErrorHandler implements RabbitListenerErrorHandler {

  private final ObjectMapper mapper;

  public BipRequestErrorHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Object handleError(
      Message amqpMessage,
      org.springframework.messaging.Message<?> message,
      ListenerExecutionFailedException exception) {
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
            mapper.readValue(e.getResponseBodyAsString(), BipPayloadResponse.class).getMessages());
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
  }
}
