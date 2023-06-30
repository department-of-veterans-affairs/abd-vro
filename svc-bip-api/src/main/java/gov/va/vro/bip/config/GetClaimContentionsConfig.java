package gov.va.vro.bip.config;

import gov.va.vro.bip.model.BipContentionResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetClaimContentionsConfig {


  @Value("${getClaimContentionsQueue}")
  String getClaimContentionsQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue getClaimContentionsQueue() {
    return new Queue(getClaimContentionsQueue, true, false, true);
  }

  @Bean
  Binding getClaimContentionsBinding() {
    return BindingBuilder.bind(getClaimContentionsQueue())
        .to(bipApiExchange)
        .with(getClaimContentionsQueue);
  }

  @Bean
  RabbitListenerErrorHandler errorHandlerForGetClaimContentions() {
    return new RabbitListenerErrorHandler() {
      @Override
      public Object handleError(
          Message amqpMessage,
          org.springframework.messaging.Message<?> message,
          ListenerExecutionFailedException exception) {

        return RMQConfig.respondToClientDueToUncaughtExcdeption(
            amqpMessage, message, exception, new BipContentionResp());
      }
    };
  }
}
