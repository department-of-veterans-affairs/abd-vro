package gov.va.vro.services.bie.config;

import gov.va.vro.services.bie.service.AmqpTopicSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KafkaConsumerCreator {

    private final List<KafkaMessageListenerContainer<?, ?>> listeners = new ArrayList<>();

    public KafkaConsumerCreator(final ConsumerFactory<?, ?> consumerFactory,
                                final AmqpTopicSender amqpTopicSender,
                                final BieProperties bieProperties) {
        setUpListeners(consumerFactory, amqpTopicSender, bieProperties.getTopicMap());
    }

    private void setUpListeners(final ConsumerFactory<?, ?> consumerFactory,
                                final AmqpTopicSender amqpTopicSender,
                                final Map<String, String> topicMap) {
        topicMap.forEach((kafkaTopic, amqpExchange) -> {
            final ContainerProperties containerProps = new ContainerProperties(kafkaTopic);
            containerProps.setAckMode(ContainerProperties.AckMode.RECORD);
            containerProps.setMessageListener((MessageListener<Integer, String>) data -> {
                log.info("event=messageReceivedFromKafka topic={} msg={}", kafkaTopic, data.value());
                amqpTopicSender.send(amqpExchange, kafkaTopic, data.value());
            });
            listeners.add(new KafkaMessageListenerContainer<>(consumerFactory, containerProps));
        });
    }

    public List<KafkaMessageListenerContainer<?, ?>> getListeners() {
        return List.copyOf(listeners);
    }

    @PostConstruct
    public void startUp() {
        listeners.forEach(AbstractMessageListenerContainer::start);
    }

    @PreDestroy
    public void tearDown() {
        listeners.forEach(AbstractMessageListenerContainer::stop);
    }
}
