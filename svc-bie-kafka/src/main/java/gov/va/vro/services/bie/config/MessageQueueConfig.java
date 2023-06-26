package gov.va.vro.services.bie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractDeclarable;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class MessageQueueConfig {

    public static final boolean DURABLE = true;
    private static final boolean AUTO_DELETE = false;

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    Declarables topicBindings(@Value("#{'${bie.topics}'.split(',')}") final List<String> topics) {
        final List<AbstractDeclarable> list = topics
                .stream()
                .map(topic -> {
                    Queue queue = new Queue(topic, DURABLE);
                    FanoutExchange fanoutExchange = new FanoutExchange(topic, DURABLE, AUTO_DELETE);
                    Binding binding = BindingBuilder.bind(queue).to(fanoutExchange);
                    return List.of(queue, fanoutExchange, binding);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info(list.toString());
        return new Declarables(list);
    }
}
