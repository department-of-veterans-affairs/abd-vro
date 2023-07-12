package gov.va.vro.bip.config;

import gov.va.vro.bip.model.BipUpdateClaimResp;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmCanCallSpecialIssueTypesConfig {


  @Value("${confirmCanCallSpecialIssueTypesQueue}")
  String confirmCanCallSpecialIssueTypesQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue confirmCanCallSpecialIssueTypesQueue() {
    return new Queue(confirmCanCallSpecialIssueTypesQueue, true, false, true);
  }

  @Bean
  Binding confirmCanCallSpecialIssueTypesBinding() {
    return BindingBuilder.bind(confirmCanCallSpecialIssueTypesQueue())
        .to(bipApiExchange)
        .with(confirmCanCallSpecialIssueTypesQueue);
  }

  @Bean
  RabbitListenerErrorHandler errorHandlerForUpdateClaimStatus() {
    return new RabbitListenerErrorHandler() {
      @Override
      public Object handleError(
          Message amqpMessage,
          org.springframework.messaging.Message<?> message,
          ListenerExecutionFailedException exception)
          throws Exception {

        return RMQConfig.respondToClientDueToUncaughtExcdeption(
            amqpMessage, message, exception, new BipUpdateClaimResp());
      }
    };
  }
}
