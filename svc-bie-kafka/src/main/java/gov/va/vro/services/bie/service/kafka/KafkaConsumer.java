package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
  public void consume(ConsumerRecord<GenericRecord, GenericRecord> record) {
    GenericRecord messageValue = record.value();
    GenericRecord messageKey = record.key();
    String topicName = record.topic();

    log.info("Topic name: {}", topicName);
    log.info("Consumed message key: {}", messageKey.toString());
    log.info("Consumed message value (before) decode: {}", messageValue.toString());

    amqpMessageSender.send(
        bieProperties.getKafkaTopicToAmqpExchangeMap().get(topicName), topicName, messageValue.toString());
  }
}
