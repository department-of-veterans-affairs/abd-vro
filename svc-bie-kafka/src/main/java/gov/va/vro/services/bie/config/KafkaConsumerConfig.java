package gov.va.vro.services.bie.config;

import gov.va.vro.services.bie.service.AmqpTopicSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private final AmqpTopicSender amqpTopicSender;

    public KafkaConsumerConfig(final AmqpTopicSender amqpTopicSender) {
        this.amqpTopicSender = amqpTopicSender;
    }

    @KafkaListener(topics = "#{'${bie.topics}'.split(',')}")
    public void bieKafkaMessageConsumer(@Payload String msg,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                        @Header(KafkaHeaders.GROUP_ID) String groupId) {
        log.info("event=messageReceivedFromKafka groupId={} topic={} msg={}", groupId, topic, msg);
        amqpTopicSender.send(topic, topic, msg);
    }
}
