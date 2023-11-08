package gov.va.vro.services.bie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.BieMessageBasePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Autowired private FanoutExchange fanoutExchange;
  @Autowired private RabbitTemplate rabbitTemplate;

  private final List<BieMessageBasePayload> receivedMessages = new ArrayList<>();
  private CountDownLatch latch;

  @RabbitListener(queues = "#{bieEventQueue.name}")
  public void receiveMqMessage(BieMessageBasePayload message) {
    log.info("Received message: {}", message);
    receivedMessages.add(message);
    latch.countDown();
  }

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Value("#{kafkaTopic}")
  private String kafkaTopic;

  @Test
  public void sendEventToKafkaTopic() throws InterruptedException, IOException {
    // Expect 2 messages in the queue
    latch = new CountDownLatch(2);

    // Message 1 goes directly to MQ
    BieMessageBasePayload msgBody = BieMessagePayloadFactory.create();
    rabbitTemplate.convertAndSend(fanoutExchange.getName(), "anyRoutingKey", msgBody);

    // Message 2 comes through Kafka
    BieMessageBasePayload kafkaEventBody = BieMessagePayloadFactory.create();
    kafkaEventBody.setEventType(null);
    kafkaEventBody.setContentionId(1234567890);

    ObjectMapper objectMapper = new ObjectMapper();
    val kafkaSentMessage = objectMapper.writeValueAsString(kafkaEventBody);
    log.info("kafkaEventBody: {}", kafkaSentMessage);

    val key = "some key";
    log.info("Producing event in Kafka topic: {}", kafkaTopic);
    kafkaTemplate.send(kafkaTopic, key, kafkaSentMessage);

    log.info("Waiting for svc-bie-kafka to publish Kafka event to RabbitMQ exchange...");
    assertTrue(latch.await(10, TimeUnit.SECONDS));

    // Check message 1
    assertEquals(msgBody, receivedMxessages.get(0));

    // Check message 2
    kafkaEventBody.setEventType(
        ContentionEvent.valueOf(ContentionEvent.mapTopicToEvent(kafkaTopic).toString()));
    assertEquals(kafkaEventBody, receivedMessages.get(1));
  }
}
