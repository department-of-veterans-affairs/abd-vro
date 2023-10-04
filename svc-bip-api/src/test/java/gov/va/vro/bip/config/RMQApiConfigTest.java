package gov.va.vro.bip.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class RMQApiConfigTest {
  @Test
  public void testRMQConfig() {
    RMQConfig rmqConfig = new RMQConfig();
    MessageConverter messageConverter = rmqConfig.jackson2MessageConverter();
    Assertions.assertNotNull(messageConverter);
    DirectExchange directExchange = rmqConfig.bipApiExchange();
    Assertions.assertNotNull(directExchange);
    RabbitListenerErrorHandler errorHandler = rmqConfig.svcBipApiErrorHandler();
    Assertions.assertNotNull(errorHandler);
  }
}
