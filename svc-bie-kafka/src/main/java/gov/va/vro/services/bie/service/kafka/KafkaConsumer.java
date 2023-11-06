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

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
  private final AmqpMessageSender amqpMessageSender;
  private final BieProperties bieProperties;

  @KafkaListener(topics = "#{bieProperties.topicNames()}")
  public void consume(ConsumerRecord<String, Object> record) {

    // TODO: Object needs to be converted to GenericRecord once local Kafka schema registry
    //  mocks are implemented for testing purposes.
    try {
      String topicName = record.topic();
      Object payload = null;
      log.info("Topic name: {}", topicName);
      if (record.value() instanceof GenericRecord) {
        payload = this.handleGenericRecord(record);
      } else if (record.value() instanceof String stringPayload) {
        log.info("Consumed message string value (before) json conversion: {}", stringPayload);
        payload = this.handleStringRecord(record);
        log.info("Sending String BieMessagePayload to Amqp Message Sender: {}", payload);
      }

      amqpMessageSender.send(ContentionEvent.rabbitMqExchangeName(topicName), topicName, payload);
    } catch (Exception e) {
      log.error("Exception occurred while processing message: " + e.getMessage());
    }
  }

  private BieMessagePayload handleStringRecord(ConsumerRecord<String, Object> record)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String messageValue = (String) record.value();
    BieMessagePayload payload = objectMapper.readValue(messageValue, BieMessagePayload.class);
    payload.setEventType(
        ContentionEvent.valueOf(ContentionEvent.mapTopicToEvent(record.topic()).name()));

    return payload;
  }

  private BieMessagePayload handleGenericRecord(ConsumerRecord<String, Object> record) {
    GenericRecord messageValue = (GenericRecord) record.value();
    String KEY_CONTENTION_CLASSIFICATION_NAME = "ContentionClassificationName";
    String KEY_DIAGNOSTIC_TYPE_CODE = "DiagnosticTypeCode";
    String KEY_CLAIM_ID = "ClaimId";
    String KEY_CONTENTION_ID = "ContentionId";
    String KEY_CONTENTION_TYPE_CODE = "ContentionTypeCode";
    String KEY_EVENT_TIME = "EventTime";
    String ACTION_NAME = "ActionName";
    String ACTION_RESULT_NAME = "ActionResultName";

    return BieMessagePayload.builder()
        .eventType(
            ContentionEvent.valueOf(ContentionEvent.mapTopicToEvent(record.topic()).toString()))
        .claimId((long) messageValue.get(KEY_CLAIM_ID))
        .contentionId((long) messageValue.get(KEY_CONTENTION_ID))
        .contentionClassificationName((String) messageValue.get(KEY_CONTENTION_CLASSIFICATION_NAME))
        .contentionTypeCode((String) messageValue.get(KEY_CONTENTION_TYPE_CODE))
        .diagnosticTypeCode((String) messageValue.get(KEY_DIAGNOSTIC_TYPE_CODE))
        .occurredAt((Long) messageValue.get(KEY_EVENT_TIME))
        .notifiedAt(record.timestamp())
        .actionName((String) messageValue.get(ACTION_NAME))
        .actionResultName((String) messageValue.get(ACTION_RESULT_NAME))
        .status(200)
        .build();
  }
}
