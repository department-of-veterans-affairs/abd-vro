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
public class KafkaConsumer {
  @Autowired AmqpMessageSender amqpMessageSender;
  @Autowired BieProperties bieProperties;

  @KafkaListener(
      topics = {"#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"})
  public void consume(
      ConsumerRecord<byte[], byte[]> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    try {
      String messageKey = new String(record.key(), StandardCharsets.UTF_8);
      String messageValue = new String(record.value(), StandardCharsets.UTF_8);

      log.info("Topic name: " + topic);
      log.info("Consumed message key: " + messageKey);
      log.info("Consumed message value (before) decode: " + messageValue);

      amqpMessageSender.send(
          bieProperties.getKafkaTopicToAmqpQueueMap().get(topic), topic, messageValue);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
    }
  }
}
