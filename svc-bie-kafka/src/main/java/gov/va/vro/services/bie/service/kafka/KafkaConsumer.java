package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
  private final AmqpMessageSender amqpMessageSender;
  private final BieProperties bieProperties;

  public ContentionEvent mapTopicToEvent(String topic) {

    log.info("TOPIC" + ContentionEvent.CONTENTION_ASSOCIATED_TO_CLAIM.getTopicName());
    if (ContentionEvent.CONTENTION_ASSOCIATED_TO_CLAIM.getTopicName().equals(topic)) {
      return ContentionEvent.CONTENTION_ASSOCIATED_TO_CLAIM;
    } else if (ContentionEvent.CONTENTION_UPDATED.getTopicName().equals(topic)) {
      return ContentionEvent.CONTENTION_UPDATED;
    } else if (ContentionEvent.CONTENTION_CLASSIFIED.getTopicName().equals(topic)) {
      return ContentionEvent.CONTENTION_CLASSIFIED;
    } else if (ContentionEvent.CONTENTION_COMPLETED.getTopicName().equals(topic)) {
      return ContentionEvent.CONTENTION_COMPLETED;
    } else if (ContentionEvent.CONTENTION_DELETED.getTopicName().equals(topic)) {
      return ContentionEvent.CONTENTION_DELETED;
    } else {
      throw new IllegalArgumentException("Unrecognized topic: " + topic);
    }
  }

  public static Map<String, Object> recordToMapIgnoringFields(
      GenericRecord record, Set<String> ignoredFields) {
    Map<String, Object> map = new HashMap<>();
    record
        .getSchema()
        .getFields()
        .forEach(
            field -> {
              if (!ignoredFields.contains(field.name())) {
                map.put(field.name(), record.get(field.name()));
              }
            });
    return map;
  }

  @KafkaListener(
      topics = {
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_UPDATED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_COMPLETED_V02",
        "#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_DELETED_V02"
      })
  public void consume(ConsumerRecord<String, GenericRecord> record) {
    try {
      GenericRecord messageValue = record.value();
      String topicName = record.topic();
      log.info("Consumed message value (before) decode: {}", messageValue);

      String KEY_DIAGNOSTIC_TYPE_CODE = "DiagnosticTypeCode";
      String KEY_CLAIM_ID = "ClaimId";
      String KEY_CONTENTION_ID = "ContentionId";
      String KEY_CONTENTION_CLASSIFICATION_NAME = "ContentionClassificationName";
      String KEY_CONTENTION_CLASSIFICATION_CODE = "ContentionClassificationCode";
      String KEY_EVENT_TIME = "EventTime";

      final Set<String> keysToRemove =
          Set.of(
              KEY_CLAIM_ID,
              KEY_CONTENTION_ID,
              KEY_CONTENTION_CLASSIFICATION_NAME,
              KEY_CONTENTION_CLASSIFICATION_CODE,
              KEY_DIAGNOSTIC_TYPE_CODE,
              KEY_EVENT_TIME);

      Map<String, Object> eventDetails = recordToMapIgnoringFields(messageValue, keysToRemove);

      BieMessagePayload payload =
          BieMessagePayload.builder()
              .eventType(ContentionEvent.valueOf(mapTopicToEvent(topicName).toString()))
              .claimId((long) messageValue.get(KEY_CLAIM_ID))
              .contentionId((long) messageValue.get(KEY_CONTENTION_ID))
              .contentionClassificationName(
                  (String) messageValue.get(KEY_CONTENTION_CLASSIFICATION_NAME))
              .contentionClassificationCode(
                  (String) messageValue.get(KEY_CONTENTION_CLASSIFICATION_CODE))
              .diagnosticTypeCode((String) messageValue.get(KEY_DIAGNOSTIC_TYPE_CODE))
              .occurredAt((Long) messageValue.get(KEY_EVENT_TIME))
              .notifiedAt(record.timestamp())
              .status(200)
              .eventDetails(eventDetails)
              .build();

      log.info("Payload: {}", payload);

      log.info("Topic name: {}", topicName);
      log.info("Consumed message value (before) decode: {}", messageValue);

      amqpMessageSender.send(
          bieProperties.getKafkaTopicToAmqpExchangeMap().get(topicName), topicName, payload);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
    }
  }
}
