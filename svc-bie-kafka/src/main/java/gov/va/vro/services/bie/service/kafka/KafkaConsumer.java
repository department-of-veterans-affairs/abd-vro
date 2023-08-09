package gov.va.vro.services.bie.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
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
    // remove first word prefix from topic seperated by _
    String noPrefixTopic = topic.substring(topic.indexOf("_") + 1);

    return Arrays.stream(ContentionEvent.values())
        .filter(event -> event.getTopicName().equals(noPrefixTopic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unrecognized topic: " + noPrefixTopic));
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
  public void consume(ConsumerRecord<String, Object> record) {
    // TODO: Object needs to be converted to GenericRecord once local Kafka schema registry
    //  mocks are implemented for testing purposes.
    try {
      String topicName = record.topic();
      Object payload = null;
      log.info("Topic name: {}", topicName);
      if (record.value() instanceof GenericRecord) {
        payload = this.handleGenericRecord(record);
        log.info(
            "Sending GenericRecord BieMessagePayload to Amqp Message Sender: {}",
            payload.toString());

      } else if (record.value() instanceof String stringPayload) {
        log.info("Consumed message string value (before) json conversion: {}", stringPayload);
        payload = this.handleStringRecord(record);
        log.info("Sending String BieMessagePayload to Amqp Message Sender: {}", payload.toString());
      }
      amqpMessageSender.send(
          bieProperties.getKafkaTopicToAmqpExchangeMap().get(topicName), topicName, payload);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
    }
  }

  private BieMessagePayload handleStringRecord(ConsumerRecord<String, Object> record)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String messageValue = (String) record.value();
    return objectMapper.readValue(messageValue, BieMessagePayload.class);
  }

  private BieMessagePayload handleGenericRecord(ConsumerRecord<String, Object> record) {
    String KEY_DIAGNOSTIC_TYPE_CODE = "DiagnosticTypeCode";
    String KEY_CLAIM_ID = "ClaimId";
    String KEY_CONTENTION_ID = "ContentionId";
    String KEY_CONTENTION_CLASSIFICATION_NAME = "ContentionClassificationName";
    String KEY_CONTENTION_TYPE_CODE = "ContentionTypeCode";
    String KEY_EVENT_TIME = "EventTime";

    GenericRecord messageValue = (GenericRecord) record.value();
    final Set<String> keysToRemove =
        Set.of(
            KEY_CLAIM_ID,
            KEY_CONTENTION_ID,
            KEY_CONTENTION_CLASSIFICATION_NAME,
            KEY_CONTENTION_TYPE_CODE,
            KEY_DIAGNOSTIC_TYPE_CODE,
            KEY_EVENT_TIME);

    Map<String, Object> eventDetails = recordToMapIgnoringFields(messageValue, keysToRemove);

    BieMessagePayload payload =
        BieMessagePayload.builder()
            .eventType(ContentionEvent.valueOf(mapTopicToEvent(record.topic()).toString()))
            .claimId((long) messageValue.get(KEY_CLAIM_ID))
            .contentionId((long) messageValue.get(KEY_CONTENTION_ID))
            .contentionClassificationName(
                (String) messageValue.get(KEY_CONTENTION_CLASSIFICATION_NAME))
            .contentionTypeCode((String) messageValue.get(KEY_CONTENTION_TYPE_CODE))
            .diagnosticTypeCode((String) messageValue.get(KEY_DIAGNOSTIC_TYPE_CODE))
            .occurredAt((Long) messageValue.get(KEY_EVENT_TIME))
            .notifiedAt(record.timestamp())
            .status(200)
            .eventDetails(eventDetails)
            .build();

    return payload;
  }
}
