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
  public void send(final String exchange, final String queue, final String message) {
    final BieMessagePayload bieMessagePayload =
        BieMessagePayload.builder()
            .event(queue)
            .notifiedAt(LocalDateTime.now().toString())
            .eventDetails(message)
            .build();
    rabbitTemplate.convertAndSend(exchange, queue, bieMessagePayload);
    log.info("event=messageSent exchange={} topic={} msg={}", exchange, queue, bieMessagePayload);
  }
}
