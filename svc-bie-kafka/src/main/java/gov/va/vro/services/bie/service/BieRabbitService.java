package gov.va.vro.services.bie.service;

import gov.va.vro.services.bie.model.BieMessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class BieRabbitService implements AmqpTopicSender {

    private final RabbitTemplate rabbitTemplate;

    public BieRabbitService(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(final String exchange, final String topic, final String message) {
        final BieMessagePayload bieMessagePayload = BieMessagePayload.builder()
                .topic(topic)
                .notifiedAt(Instant.now().toString())
                .event(message)
                .build();
        rabbitTemplate.convertAndSend(exchange, topic, bieMessagePayload);
        log.info("event=messageSent exchange={} topic={} msg={}", exchange, topic, bieMessagePayload);
    }
}
