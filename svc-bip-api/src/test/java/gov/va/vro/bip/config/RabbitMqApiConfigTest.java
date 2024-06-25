package gov.va.vro.bip.config;

import com.datadog.api.client.v1.api.MetricsApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.service.BipRequestErrorHandler;
import gov.va.vro.bip.service.MetricLoggerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;

class RabbitMqApiConfigTest {

  private final MetricLoggerService metricLoggerService = new MetricLoggerService(new MetricsApi());

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testRabbitMqConfig() {
    RabbitMqConfig rmqConfig =
        new RabbitMqConfig(
            new RabbitMqConfigProperties(), new JacksonConfig(), metricLoggerService);
    DirectExchange directExchange = rmqConfig.bipApiExchange();
    Assertions.assertNotNull(directExchange);
    RabbitListenerErrorHandler errorHandler =
        new BipRequestErrorHandler(new ObjectMapper(), metricLoggerService);
    try {
      var headers = new HashMap<>();
      headers.put("replyChannel", "mock ReplyChannel");

      var handledError =
          errorHandler.handleError(
              new Message("mock".getBytes()),
              new GenericMessage("foo", headers),
              new ListenerExecutionFailedException("mock exception", new Exception("oops")));
      Assertions.assertNotNull(handledError);
    } catch (Exception e) {
      Assertions.fail(e);
    }
    Assertions.assertNotNull(errorHandler);
  }
}
