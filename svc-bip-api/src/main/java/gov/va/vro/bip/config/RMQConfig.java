package gov.va.vro.bip.config;

import gov.va.vro.bip.model.HasStatusCodeAndMessage;
import gov.va.vro.bip.service.RMQController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Configuration
@Slf4j
public class RMQConfig {
  @Bean
  RMQController controller() {
    return new RMQController();
  }

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

  static String getStackTrace(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  public static Object respondToClientDueToUncaughtExcdeption(
      Message amqpMessage,
      org.springframework.messaging.Message<?> message,
      ListenerExecutionFailedException exception,
      HasStatusCodeAndMessage rVal) {
    try {
      UUID errorId = UUID.randomUUID();

      log.error(
          "ListenerExecutionFailedException occurred because of:{}\n "
              + "And the failed message was {}.\n  "
              + "The error id reported to client was {}",
          getStackTrace(exception.getCause()),
          message.toString(),
          errorId);
      String messageStr =
          "There was a system error while processing your request.  "
              + "Please contact VRO support with error number "
              + errorId
              + " if the problem persists.";
      rVal.statusCode = 500;
      rVal.statusMessage = messageStr;
      return rVal;
    } catch (Exception e) {
      log.error(
          "An uncaught exception was thrown from within default error handler.  "
              + "This is really bad. Terminating process",
          e);
      System.exit(-1);
    }
    return null;
  }
}
