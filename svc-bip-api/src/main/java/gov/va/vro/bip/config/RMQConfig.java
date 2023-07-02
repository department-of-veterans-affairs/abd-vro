package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RMQConfig {
    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    DirectExchange bipApiExchange() {
        return new DirectExchange("bipApiExchange", true, true);
    }

    //getClaimDetailsQueue (not a getter.  the "get" referes to the name of the api endpoint)
    @Bean
    Queue getClaimDetailsQueue() {
        return new Queue("getClaimDetailsQueue", true, false, true);
    }
    @Bean
    Binding getClaimDetailsBinding() {
        return BindingBuilder.bind(getClaimDetailsQueue()).to(bipApiExchange()).with("getClaimDetailsQueue");
    }

    //setClaimToRfdStatus (not a setter)
    @Bean
    Queue setClaimToRfdStatusQueue() {
        return new Queue("setClaimToRfdStatusQueue", true, false, true);
    }
    @Bean
    Binding setClaimToRfdStatusBinding() {
        return BindingBuilder.bind(setClaimToRfdStatusQueue()).to(bipApiExchange()).with("setClaimToRfdStatusQueue");
    }

    //updateClaimStatus
    @Bean
    Queue updateClaimStatusQueue() {
        return new Queue("updateClaimStatusQueue", true, false, true);
    }
    @Bean
    Binding updateClaimStatusBinding() {
        return BindingBuilder.bind(updateClaimStatusQueue()).to(bipApiExchange()).with("updateClaimStatusQueue");
    }

    //getClaimContentions
    @Bean
    Queue getClaimContentionsQueue() {
        return new Queue("getClaimContentionsQueue", true, false, true);
    }
    @Bean
    Binding getClaimContentionsBinding() {
        return BindingBuilder.bind(getClaimContentionsQueue()).to(bipApiExchange()).with("getClaimContentionsQueue");
    }

    //updateClaimContention
    @Bean
    Queue updateClaimContentionQueue() {
        return new Queue("updateClaimContentionQueue", true, false, true);
    }
    @Bean
    Binding updateClaimContentionBinding() {
        return BindingBuilder.bind(updateClaimContentionQueue()).to(bipApiExchange()).with("updateClaimContentionQueue");
    }

    //verifySpecialIssueTypes
    @Bean
    Queue verifySpecialIssueTypesQueue() {
        return new Queue("verifySpecialIssueTypesQueue", true, false, true);
    }
    @Bean
    Binding verifySpecialIssueTypesBinding() {
        return BindingBuilder.bind(verifySpecialIssueTypesQueue()).to(bipApiExchange()).with("verifySpecialIssueTypesQueue");
    }

}



