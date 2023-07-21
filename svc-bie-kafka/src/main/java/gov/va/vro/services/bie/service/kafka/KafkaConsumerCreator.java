package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerCreator {
  @Autowired AmqpMessageSender amqpMessageSender;
  @Autowired BieProperties bieProperties;

  @KafkaListener(
      topics = {"#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"})
  public void consume(
      ConsumerRecord<GenericRecord, GenericRecord> record,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    GenericRecord key = record.key();
    GenericRecord value = record.value();
    log.info("Consumed message key: " + key);
    log.info("Consumed message value: " + value);
    log.info("Consumed message value (toString): " + value.toString());
    amqpMessageSender.send(
        bieProperties.getKafkaTopicToAmqpQueueMap().get(topic), topic, value.toString());
  }
}
