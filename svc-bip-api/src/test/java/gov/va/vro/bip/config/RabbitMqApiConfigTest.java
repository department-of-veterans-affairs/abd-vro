package gov.va.vro.bip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.service.BipRequestErrorHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;

class RabbitMqApiConfigTest {

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testRabbitMqConfig() {
    RabbitMqConfig rmqConfig = new RabbitMqConfig();
    MessageConverter messageConverter = rmqConfig.jackson2MessageConverter();
    Assertions.assertNotNull(messageConverter);
    DirectExchange directExchange = rmqConfig.bipApiExchange();
    Assertions.assertNotNull(directExchange);
    RabbitListenerErrorHandler errorHandler = new BipRequestErrorHandler(new ObjectMapper());
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
