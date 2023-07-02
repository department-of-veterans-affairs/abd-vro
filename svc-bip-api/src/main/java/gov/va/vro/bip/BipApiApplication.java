package gov.va.vro.bip;

import gov.va.vro.model.xample.SomeDtoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class BipApiApplication {
    @SpringBootApplication
    public class AbdApplication {
        public static void main(String[] args) {
            SpringApplication.run(BipApiApplication.class, args);
        }
    }
    @Bean
    DirectExchange bipApiExchange() {
        return new DirectExchange("bipApiExchange", true, true);
    }
    @Bean
    Queue getClaimDetailsQueue() {
        return new Queue("getClaimDetailsQueue", true, false, true);
    }
    @Bean
    Binding getClaimDetailsBinding() {
        return BindingBuilder.bind(getClaimDetailsQueue()).to(bipApiExchange()).with("getClaimDetailsQueue");
    }



    @Bean
    public MessageConverter jackson2MessageConverter() {
        var converter = new Jackson2JsonMessageConverter();
        // converter.setAlwaysConvertToInferredType(true);
        return converter;
    }



}
