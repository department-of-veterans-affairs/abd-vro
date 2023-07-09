package gov.va.vro.bip.config;

import gov.va.vro.bip.model.BipClaim;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigForGetClaimDetails {

  @Value("${getClaimDetailsQueue}")
  String getClaimDetailsQueue;
  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue getClaimDetailsQueue() {
    return new Queue(getClaimDetailsQueue, true, false, true);
  }

  @Bean
  Binding getClaimDetailsBinding() {
    return BindingBuilder.bind(getClaimDetailsQueue())
        .to(bipApiExchange)
        .with(getClaimDetailsQueue);
  }

  @Bean
  RabbitListenerErrorHandler errorHandlerForGetClaimDetails() {
    return new RabbitListenerErrorHandler() {
      @Override
      public Object handleError(
          Message amqpMessage,
          org.springframework.messaging.Message<?> message,
          ListenerExecutionFailedException exception)
          throws Exception {

        return RMQConfig.respondToClientDueToUncaughtExcdeption(
            amqpMessage, message, exception, new BipClaim());
      }
    };
  }
}
