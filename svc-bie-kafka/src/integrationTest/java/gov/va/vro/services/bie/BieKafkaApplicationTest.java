package gov.va.vro.services.bie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.BieMessagePayload;
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
  @Autowired private ObjectMapper objectMapper;

  private final List<BieMessagePayload> receivedMessages = new ArrayList<>();
  private CountDownLatch latch;

  @RabbitListener(queues = "#{bieEventQueue.name}")
  public void receiveMqMessage(BieMessagePayload message) {
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
    BieMessagePayload msgBody = BieMessagePayloadFactory.create();
    rabbitTemplate.convertAndSend(fanoutExchange.getName(), "anyRoutingKey", msgBody);

    // Message 2 comes through Kafka
    String kafkaEventBody =
        "{\"contentionId\": \"4562323232\", \"contentionTypeCode\": \"123\", \"contentionClassificationName\": \"some name\", \"claimId\": \"1232323232\", \"diagnosticTypeCode\": \"some code\", \"occurredAt\": \"1692649506\", \"notifiedAt\": \"1692649506\", \"status\": \"200\"}";

    val key = "some key";
    log.info("Producing event in Kafka topic: {}", kafkaTopic);
    kafkaTemplate.send(kafkaTopic, key, kafkaEventBody);

    log.info("Waiting for svc-bie-kafka to publish Kafka event to RabbitMQ exchange...");
    assertTrue(latch.await(10, TimeUnit.SECONDS));

    // Check message 1
    assertEquals(msgBody, receivedMessages.get(0));

    // Check message 2
    val kafkaSentMessage = objectMapper.readValue(kafkaEventBody, BieMessagePayload.class);
    assertEquals(kafkaSentMessage, receivedMessages.get(1));
  }
}
