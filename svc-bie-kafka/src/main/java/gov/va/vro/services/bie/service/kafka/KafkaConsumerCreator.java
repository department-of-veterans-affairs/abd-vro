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
import java.util.Base64;

@Slf4j
@Component
public class KafkaConsumerCreator {
  @Autowired AmqpMessageSender amqpMessageSender;
  @Autowired BieProperties bieProperties;

  @KafkaListener(
      topics = {"#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"})
  public void consume(
      ConsumerRecord<Long, byte[]> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    byte[] decodedBytes = Base64.getDecoder().decode(record.value());
    String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

    log.info("Consumed message key: " + record.key());
    log.info("Consumed message value: " + decodedString);
    amqpMessageSender.send(
        bieProperties.getKafkaTopicToAmqpQueueMap().get(topic), topic, decodedString);
  }
}
