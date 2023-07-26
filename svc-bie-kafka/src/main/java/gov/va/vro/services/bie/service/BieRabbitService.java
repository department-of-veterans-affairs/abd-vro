package gov.va.vro.services.bie.service;

import gov.va.vro.model.biekafka.BieMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class BieRabbitService implements AmqpMessageSender {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void send(final String exchange, final String routingKey, final String message) {
    final BieMessagePayload bieMessagePayload =
        BieMessagePayload.builder()
            .event(routingKey)
            .notifiedAt(LocalDateTime.now().toString())
            .eventDetails(message)
            .build();
    rabbitTemplate.convertAndSend(exchange, routingKey, bieMessagePayload);
    log.debug(
        "event=messageSent exchange={} topic={} msg={}", exchange, routingKey, bieMessagePayload);
  }
}
