package gov.va.vro.bip.config;
import java.util.List;
import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimContention;
import gov.va.vro.model.xample.SomeDtoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RMQErrorHandler {
    public static Map<String, Class> qNameMapReturnType;
    static {
        qNameMapReturnType = new HashMap<>();
        qNameMapReturnType.put("getClaimDetailsQueue", BipClaim.class);
        qNameMapReturnType.put("setClaimToRfdStatusQueue", BipUpdateClaimResp.class);
        qNameMapReturnType.put("updateClaimStatusQueue", BipUpdateClaimResp.class);
        //articleMapOne.put("getClaimContentionsQueue", List<ClaimContention>.class);
        qNameMapReturnType.put("updateClaimContentionQueue", BipUpdateClaimResp.class);

    }
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
     */
    public static Object handleError(
            Message amqpMessage,
            org.springframework.messaging.Message<?> message,
            ListenerExecutionFailedException exception) throws Exception {

        log.error("ListenerExecutionFailedException occurred because of:{}.  " +
                "And the fialed message was {}. ", exception.getCause(), message.toString());
        String dstQ = exception.getFailedMessage().getMessageProperties().getConsumerQueue();
        String messageStr = "There was a system error while processing your request.  "+
                ".  Please contact VRO support if the problem persists.";
        return new BipClaim(500, messageStr);
        //todo: this method needs to return whatever type the client expects.
    }
}