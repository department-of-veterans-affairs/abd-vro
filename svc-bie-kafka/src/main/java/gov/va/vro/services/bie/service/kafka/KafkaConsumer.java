package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
  private final AmqpMessageSender amqpMessageSender;
  private final BieProperties bieProperties;

  @KafkaListener(
      topics = {
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_UPDATED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_COMPLETED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_DELETED_V02"
      })
  public void consume(ConsumerRecord<byte[], byte[]> record) {
    try {
      String messageKey = new String(record.key(), StandardCharsets.UTF_8);
      String messageValue = new String(record.value(), StandardCharsets.UTF_8);
      String topicName = record.topic();

      log.info("Topic name: {}", topicName);
      log.info("Consumed message key: {}", messageKey);
      log.info("Consumed message value (before) decode: {}", messageValue);

      amqpMessageSender.send(
          bieProperties.getKafkaTopicToAmqpExchangeMap().get(topicName), topicName, messageValue);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
    }
  }
}
