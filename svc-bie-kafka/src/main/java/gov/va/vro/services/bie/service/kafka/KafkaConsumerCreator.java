package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
@Slf4j
@Component
public class KafkaConsumerCreator {
  @Autowired AmqpMessageSender amqpMessageSender;
  @Autowired BieProperties bieProperties;

  @KafkaListener(
      topics = {
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_UPDATED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_COMPLETED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_DELETED_V02"
      })
  public void consume(ConsumerRecord<byte[], byte[]> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    String key = new String(record.key(), StandardCharsets.UTF_8);
    String value = new String(record.value(), StandardCharsets.UTF_8);
    log.info("Consumed message key: " + key);
    log.info("Consumed message value: " + value);
    amqpMessageSender.send(bieProperties.getKafkaTopicToAmqpQueueMap().get(topic), topic, value);
  }
}
