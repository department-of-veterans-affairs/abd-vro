package gov.va.vro.services.bie;

import static gov.va.vro.services.bie.IntegrationTestConfig.MQ_QUEUE;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
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

  @Autowired private RabbitTemplate rabbitTemplate;

  List<Message> receivedMessages = new ArrayList<>();

  @RabbitListener(queues = "#{queue1.name}")
  public void receiveMqMessage(Message message) {
    log.info("===========Received: " + message);
    receivedMessages.add(message);
  }

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  private static final String KAFKA_TOPIC = "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02";
  static final String MQ_EXCHANGE = "bie-events-contention-associated";
  String message = "=========integration test message";

  @Test
  public void sendMessage() throws InterruptedException {
    rabbitTemplate.convertAndSend(MQ_EXCHANGE, "doesn't matter", "========Test mq message");

    log.info("Sleeping...");
    Thread.sleep(10000);
    kafkaTemplate.send(KAFKA_TOPIC, message);
    Thread.sleep(10000);
    log.info("Received Messages: " + printMessages(receivedMessages));
  }

  String printMessages(List<Message> messages) {
    String delimiter = "\n  ";
    return delimiter+messages.stream().map(Object::toString).collect(Collectors.joining(delimiter)).toString();
  }
}
