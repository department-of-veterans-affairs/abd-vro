package gov.va.vro.bip.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateClaimStatusConfig {


  @Value("${updateClaimStatusQueue}")
  String updateClaimStatusQueue;

  @Autowired DirectExchange bipApiExchange;

  @Bean
  Queue updateClaimStatusQueue() {
    return new Queue(updateClaimStatusQueue, true, false, true);
  }

  @Bean
  Binding updateClaimStatusBinding() {
    return BindingBuilder.bind(updateClaimStatusQueue())
        .to(bipApiExchange)
        .with(updateClaimStatusQueue);
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
