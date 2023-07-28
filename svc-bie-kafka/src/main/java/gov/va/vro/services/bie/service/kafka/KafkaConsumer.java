package gov.va.vro.services.bie.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionKafkaEventType;
import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
  private final AmqpMessageSender amqpMessageSender;
  private final BieProperties bieProperties;

  public ContentionKafkaEventType mapTopicToEvent(String topic) {

    log.info("TOPIC" + ContentionKafkaEventType.CONTENTION_ASSOCIATED_TO_CLAIM.getTopicName());
    if (ContentionKafkaEventType.CONTENTION_ASSOCIATED_TO_CLAIM.getTopicName().equals(topic)) {
      return ContentionKafkaEventType.CONTENTION_ASSOCIATED_TO_CLAIM;
    } else if (ContentionKafkaEventType.CONTENTION_UPDATED.getTopicName().equals(topic)) {
      return ContentionKafkaEventType.CONTENTION_UPDATED;
    } else if (ContentionKafkaEventType.CONTENTION_CLASSIFIED.getTopicName().equals(topic)) {
      return ContentionKafkaEventType.CONTENTION_CLASSIFIED;
    } else if (ContentionKafkaEventType.CONTENTION_COMPLETED.getTopicName().equals(topic)) {
      return ContentionKafkaEventType.CONTENTION_COMPLETED;
    } else if (ContentionKafkaEventType.CONTENTION_DELETED.getTopicName().equals(topic)) {
      return ContentionKafkaEventType.CONTENTION_DELETED;
    } else {
      throw new IllegalArgumentException("Unrecognized topic: " + topic);
    }
  }

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
      log.info("Consumed record value: {}", new String(record.value(), StandardCharsets.UTF_8));
      //      String messageKey = new String(record.key(), StandardCharsets.UTF_8);
      String messageValue = new String(record.value(), StandardCharsets.UTF_8);
      String topicName = record.topic();
      //      log.info("Consumed message key: {}", messageKey);
      log.info("Consumed message value (before) decode: {}", messageValue);
      // convert messageValue to json using jackson
      final ObjectMapper objectMapper = new ObjectMapper();
      final Map<String, Object> jsonBody = objectMapper.readValue(messageValue, Map.class);

      log.info("JSONBODY: " + jsonBody);

      final var keysToRemove =
          Arrays.asList(
              "ClaimId", "DiagnosticTypeCode", "ContentionClassificationName", "EventTime", "ContentionId");
      Map<String, Object> eventDetails =
          jsonBody.entrySet().stream()
              .filter(entry -> !keysToRemove.contains(entry.getKey()))
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));

      BieMessagePayload payload =
          BieMessagePayload.builder()
              .eventType(ContentionKafkaEventType.valueOf(mapTopicToEvent(topicName).toString()))
              .claimId((long) jsonBody.get("ClaimId"))
              .contentionId((long) jsonBody.get("ContentionId"))
              .diagnosticTypeCode((String) jsonBody.get("DiagnosticTypeCode"))
              .contentionClassificationName((String) jsonBody.get("ContentionClassificationName"))
              .occurredAt((Long) jsonBody.get("EventTime"))
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
