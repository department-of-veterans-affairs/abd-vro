package gov.va.vro.services.bie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

  private List<Message> receivedMessages = new ArrayList<>();
  private CountDownLatch latch;

  @RabbitListener(queues = "#{bieEventQueue.name}")
  public void receiveMqMessage(Message message) {
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

    // Message 1
    String msgBody = "Message to ensure MQ's fanout exchange is working";
    rabbitTemplate.convertAndSend(fanoutExchange.getName(), "anyRoutingKey", msgBody);

    // Message 2
    String kafkaEventBody = "a Kafka event payload";
    log.info("Producing event in Kafka topic: {}", kafkaTopic);
    kafkaTemplate.send(kafkaTopic, kafkaEventBody);

    log.info("Waiting for svc-bie-kafka to publish Kafka event to RabbitMQ exchange...");
    assertTrue(latch.await(20, TimeUnit.SECONDS));

    log.info("Received Messages: " + printMessages(receivedMessages, "\n  "));

    // Check message 1
    assertEquals(msgBody, objectMapper.readValue(receivedMessages.get(0).getBody(), String.class));

    // Check message 2
    // Read Kafka event as a generic JSON object
    val jsonObj =
        objectMapper.readValue(
            receivedMessages.get(1).getBody(), new TypeReference<HashMap<String, Object>>() {});
    assertEquals(kafkaEventBody, jsonObj.get("eventDetails"));
  }

  static String printMessages(List<Message> messages, String delimiter) {
    return delimiter
        + messages.stream().map(Object::toString).collect(Collectors.joining(delimiter)).toString();
  }
}
