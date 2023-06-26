package gov.va.vro.services.bie.config;

import gov.va.vro.services.bie.model.BieMessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.text.MessageFormat;
import java.time.Instant;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private final RabbitTemplate rabbitTemplate;

    public KafkaConsumerConfig(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @KafkaListener(groupId = "${bie.kafka.groupId}", topics = "#{'${bie.topics}'.split(',')}")
    public void consumerBieKafkaMessage(@Payload String msg,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                        @Header(KafkaHeaders.GROUP_ID) String groupId) {
        final BieMessagePayload bieMessagePayload = BieMessagePayload.builder()
                .topic(topic)
                .notifiedAt(Instant.now().toString())
                .event(msg)
                .build();
        log.info(MessageFormat.format("event=messageReceivedFromKafka groupId={0} topic={1} msg={2}", groupId, topic, bieMessagePayload));
        final String exchange = topic;
        rabbitTemplate.convertAndSend(exchange, topic, bieMessagePayload);
        log.info(MessageFormat.format("event=messageSentToQueue exchange={0} topic={1} msg={2}", exchange, topic, bieMessagePayload));
    }
}
