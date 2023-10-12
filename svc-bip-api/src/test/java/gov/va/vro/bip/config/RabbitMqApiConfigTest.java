package gov.va.vro.bip.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
class RabbitMqApiConfigTest {
  @Test
  public void testRabbitMqConfig() {
    RabbitMqConfig rmqConfig = new RabbitMqConfig();
    MessageConverter messageConverter = rmqConfig.jackson2MessageConverter();
    Assertions.assertNotNull(messageConverter);
    DirectExchange directExchange = rmqConfig.bipApiExchange();
    Assertions.assertNotNull(directExchange);
    RabbitListenerErrorHandler errorHandler = rmqConfig.svcBipApiErrorHandler();
    try {
      Map headers = new HashMap();
      headers.put("replyChannel", "mock ReplyChannel");
      var handledError =
          errorHandler.handleError(
              new Message("mock".getBytes()),
              new GenericMessage("foo", headers),
              new ListenerExecutionFailedException("mock exception", null));
      Assertions.assertNotNull(handledError);
    } catch (Exception e) {
      Assertions.fail(e);
    }
    Assertions.assertNotNull(errorHandler);
  }
}
