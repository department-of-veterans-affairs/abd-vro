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
public class ConfigForSetClaimToRfdStatus {
  @Value("${setClaimToRfdStatusQueue}")
  String setClaimToRfdStatusQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue setClaimToRfdStatusQueue() {
    return new Queue(setClaimToRfdStatusQueue, true, false, true);
  }

  @Bean
  Binding setClaimToRfdStatusBinding() {
    return BindingBuilder.bind(setClaimToRfdStatusQueue())
        .to(bipApiExchange)
        .with(setClaimToRfdStatusQueue);
  }

  @Bean
  RabbitListenerErrorHandler errorHandlerForSetClaimToRfdStatus() {
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
