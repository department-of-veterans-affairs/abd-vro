package gov.va.vro.bip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipMessage;
import gov.va.vro.bip.model.BipPayloadResponse;
import gov.va.vro.metricslogging.IMetricLoggerService;
import gov.va.vro.metricslogging.MetricLoggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
@ComponentScan("gov.va.vro.metricslogging")
public class BipRequestErrorHandler implements RabbitListenerErrorHandler {

  private final ObjectMapper mapper;

  private final IMetricLoggerService metricLoggerService;

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

      List<BipMessage> messages = new ArrayList<>();
      if (!e.getResponseBodyAsString().isBlank()) {
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
      }

      metricLoggerService.submitCount(
          MetricLoggerService.METRIC.LISTENER_ERROR,
          new String[] {
            "event:statusCodeException",
            String.format("statusCode:%d", status),
            String.format("statusMessage:%s", statusMessage),
            "source:BipRequestErrorHandler",
            String.format("error:%s", exception.getMessage())
          });

      return BipPayloadResponse.builder()
          .statusCode(status)
          .statusMessage(statusMessage)
          .messages(messages)
          .build();

    } else {
      log.error("Unexpected Error in svc-bip-api", exception);
      String timestamp = Instant.now().toString();
      List<BipMessage> errs =
          List.of(
              BipMessage.builder()
                  .key(this.getClass().getSimpleName())
                  .severity("FATAL")
                  .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                  .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.name())
                  .text("Unexpected error in svc-bip-api: " + exception.getCause().getMessage())
                  .timestamp(timestamp)
                  .build());

      metricLoggerService.submitCount(
          MetricLoggerService.METRIC.LISTENER_ERROR,
          new String[] {
            "event:unexpectedError",
            "source:BipRequestErrorHandler",
            String.format("error:%s", exception.getMessage())
          });

      return BipPayloadResponse.builder()
          .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .statusMessage(HttpStatus.INTERNAL_SERVER_ERROR.name())
          .messages(errs)
          .build();
    }
  }
}
