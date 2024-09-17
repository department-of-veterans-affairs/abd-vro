package gov.va.vro.services.bie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import gov.va.vro.services.bie.service.amqp.AmqpMessageSender;
import gov.va.vro.services.bie.service.repo.ContentionEventsRepo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Start RabbitMQ: docker compose up -d rabbitmq-service
 *
 * <p>Start mock Kafka: COMPOSE_PROFILES="kafka" ./gradlew -p mocks :dockerComposeUp
 *
 * <p>Run tests: ./gradlew :svc-bie-kafka:integrationTest --rerun-tasks
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Slf4j
public class BieKafkaApplicationTest {

  @SpyBean @Autowired private AmqpMessageSender bieRabbitService;
  @SpyBean @Autowired private ContentionEventsRepo contentionEventsRepo;

  private final List<ContentionEventPayload> receivedMessages = new ArrayList<>();
  private CountDownLatch latch;

  @RabbitListener(queues = "#{bieEventQueue.name}")
  public void receiveMqMessage(ContentionEventPayload message) {
    log.info("Received message: {}", message);
    receivedMessages.add(message);
    latch.countDown();
  }

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Value("#{kafkaTopic}")
  private String kafkaTopic;

  @Test
  public void sendEventToKafkaTopic() throws InterruptedException, IOException {
    // Expect 1 messages in the queue
    latch = new CountDownLatch(1);

    // Message 2 comes through Kafka
    ContentionEventPayload kafkaEventBody = BieMessagePayloadFactory.create();
    kafkaEventBody.setEventType(null);
    kafkaEventBody.setContentionId(1234567890L);
    kafkaEventBody.setNotifiedAt(1L);

    ObjectMapper objectMapper = new ObjectMapper();
    val kafkaSentMessage = objectMapper.writeValueAsString(kafkaEventBody);
    log.info("kafkaEventBody: {}", kafkaSentMessage);

    val key = "some key";
    log.info("Producing event in Kafka topic: {}", kafkaTopic);
    kafkaTemplate.send(kafkaTopic, null, 1L, key, kafkaSentMessage);

    log.info("Waiting for svc-bie-kafka to publish Kafka event to RabbitMQ exchange...");
    assertTrue(latch.await(20, TimeUnit.SECONDS));

    // Check message 2
    kafkaEventBody.setEventType(
        ContentionEvent.valueOf(ContentionEvent.mapTopicToEvent(kafkaTopic).toString()));
    assertEquals(kafkaEventBody, receivedMessages.get(0));
    verify(bieRabbitService, times(1)).send(any(), any(), any());
    verify(contentionEventsRepo, times(1)).save(any());
  }
}
