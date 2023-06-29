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
import java.util.regex.Pattern;

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
        topicMap.forEach((kafkaTopicPattern, amqpExchange) -> {
            final Pattern topicPattern = Pattern.compile(".*" + kafkaTopicPattern + ".*");
            final ContainerProperties containerProps = new ContainerProperties(topicPattern);
            containerProps.setAckMode(ContainerProperties.AckMode.RECORD);
            containerProps.setMessageListener((MessageListener<Integer, String>) data -> {
                log.info("event=messageReceivedFromKafka topic={} msg={}", data.topic(), data.value());
                amqpTopicSender.send(amqpExchange, data.topic(), data.value());
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
