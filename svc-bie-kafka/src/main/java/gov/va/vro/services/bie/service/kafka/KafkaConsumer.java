package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
  private final AmqpMessageSender amqpMessageSender;
  private final BieProperties bieProperties;
  private final String KEY_DIAGNOSTIC_TYPE_CODE = "DiagnosticTypeCode";
  private final String KEY_CLAIM_ID = "ClaimId";
  private final String KEY_CONTENTION_ID = "ContentionId";
  private final String KEY_CONTENTION_CLASSIFICATION_NAME = "ContentionClassificationName";
  private final String KEY_CONTENTION_TYPE_CODE = "ContentionTypeCode";
  private final String KEY_EVENT_TIME = "EventTime";
  private final String[] INCLUDED_FIELDS =
      new String[] {
        KEY_DIAGNOSTIC_TYPE_CODE,
        KEY_CLAIM_ID,
        KEY_CONTENTION_ID,
        KEY_CONTENTION_CLASSIFICATION_NAME,
        KEY_CONTENTION_TYPE_CODE,
        KEY_EVENT_TIME
      };

  @KafkaListener(
      topics = {
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_UPDATED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_COMPLETED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_DELETED_V02"
      })
  public void consume(ConsumerRecord<String, Object> record) {
    String messageValue = null;
    String topicName = record.topic();

    if (record.value() instanceof GenericRecord value) {
      GenericData.Record newRecord = new GenericData.Record(value.getSchema());

      Arrays.stream(INCLUDED_FIELDS)
          .filter(value::hasField)
          .forEach(field -> newRecord.put(field, value.get(field)));

      messageValue = newRecord.toString();
      log.info("Kafka message value {}", newRecord);
    } else if (record.value() instanceof String stringValue) {
      messageValue = stringValue;
    }

    amqpMessageSender.send(
        bieProperties.getKafkaTopicToAmqpExchangeMap().get(topicName), topicName, messageValue);
  }
}
