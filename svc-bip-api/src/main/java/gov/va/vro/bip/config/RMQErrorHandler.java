package gov.va.vro.bip.config;

import gov.va.vro.model.xample.SomeDtoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class RMQErrorHandler {
    /**
     * Boilerplate that regiesters error handler method with the system
     */
    @Bean
    RabbitListenerErrorHandler rabbitListenerErrorHandler() {
        return new RabbitListenerErrorHandler() {
            @Override
            public Object handleError(
                    Message amqpMessage,
                    org.springframework.messaging.Message<?> message,
                    ListenerExecutionFailedException exception) throws Exception {
                return  RMQErrorHandler.handleError(amqpMessage,message,exception);
            }
        };
    }

    /**
     * Handles uncaught exceptions that occur during processing of messages from queue.
     * todo: vary the message set to client depending on the cause of exception.  For example
     * if the cause is MessageConversionException, we should inform the client of expected format.
     */
    public static Object handleError(
            Message amqpMessage,
            org.springframework.messaging.Message<?> message,
            ListenerExecutionFailedException exception) throws Exception {

        log.error("ListenerExecutionFailedException occurred. ", exception.getCause());
        String correlationId = amqpMessage.getMessageProperties().getCorrelationId().toString();
        String messageStr = "{\"msg\":\"There was a system error while processing your request with correlationId " + correlationId +
                ".  Please contact VRO support if the problem persists.\"}";
        Message response = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
        response.getMessageProperties().setCorrelationId(correlationId);
        response.getMessageProperties().getHeaders().put("status", "500");
        log.info("Responding with: {}", response.toString());
        return response;
    }
}