package gov.va.vro.services.bie.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.model.biekafka.annotation.Ignore;
import gov.va.vro.model.biekafka.annotation.TargetEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BieRecordTransformer {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public ContentionEventPayload toContentionEventPayload(ConsumerRecord<String, Object> record)
      throws JsonProcessingException, IllegalStateException {
    String topicName = record.topic();
    ContentionEvent contentionEvent =
        ContentionEvent.valueOf(ContentionEvent.mapTopicToEvent(record.topic()).name());
    log.info("Topic name: {}", topicName);

    ContentionEventPayload payload;
    if (record.value() instanceof GenericRecord) {
      payload = this.handleGenericRecord(contentionEvent, record);
      log.info("kafka payload: {}", payload);
    } else if (record.value() instanceof String stringPayload) {
      log.info("Consumed message string value (before) json conversion: {}", stringPayload);
      payload = this.handleStringRecord(record);
      log.info("Sending String BieMessagePayload to Amqp Message Sender: {}", payload);
    } else {
      throw new IllegalStateException("Unsupported record type");
    }
    payload.setEventType(contentionEvent);
    payload.setNotifiedAt(record.timestamp());
    payload.setStatus(200);
    return payload;
  }

  private ContentionEventPayload handleStringRecord(ConsumerRecord<String, Object> record)
      throws JsonProcessingException {
    String messageValue = (String) record.value();

    return objectMapper.readValue(messageValue, ContentionEventPayload.class);
  }

  private ContentionEventPayload handleGenericRecord(
      ContentionEvent contentionEvent, ConsumerRecord<String, Object> record) {
    GenericRecord messageValue = (GenericRecord) record.value();
    return BieRecordTransformer.processBieMessagePayloadFields(contentionEvent, messageValue);
  }

  private static ContentionEventPayload processBieMessagePayloadFields(
      ContentionEvent contentionEvent, GenericRecord genericRecord) {
    ContentionEventPayload payload = ContentionEventPayload.builder().build();

    for (Field field : ContentionEventPayload.class.getDeclaredFields()) {
      String fieldName = field.getName();

      // Skip the field if it has @Ignore annotation or is null
      if (field.isAnnotationPresent(Ignore.class)) {
        continue;
      }

      if (field.isAnnotationPresent(TargetEvents.class)) {
        String[] annotationValues = field.getAnnotation(TargetEvents.class).value();

        if (!isValidTopicNames(annotationValues)) {
          throw new IllegalArgumentException(
              "Invalid topic names in annotation for field: " + fieldName);
        }

        boolean matchedTopicName =
            Arrays.stream(annotationValues).anyMatch(v -> v.equals(contentionEvent.getTopicName()));

        if (matchedTopicName) {
          invokeSetterMethod(payload, field, genericRecord);
        }
      } else {
        invokeSetterMethod(payload, field, genericRecord);
      }
    }

    return payload;
  }

  private static void invokeSetterMethod(
      ContentionEventPayload payload, Field field, GenericRecord genericRecord) {
    String fieldName = field.getName();
    String capitalizedFieldName = StringUtils.capitalize(fieldName);

    String setterMethodName = "set" + capitalizedFieldName;

    // if GenericRecord doesn't have the value, we do not process further
    if (!genericRecord.hasField(capitalizedFieldName)) {
      return;
    }

    Object value = genericRecord.get(capitalizedFieldName);
    try {
      Method setterMethod =
          ContentionEventPayload.class.getMethod(setterMethodName, field.getType());

      if (Objects.isNull(value) || field.getType().isAssignableFrom(value.getClass())) {
        setterMethod.invoke(payload, value);
      } else {
        log.warn(
            "Type mismatch for field '{}'. Expected type: '{}', Actual value: '{}'",
            fieldName,
            field.getType().getSimpleName(),
            value);
      }
    } catch (Exception e) {
      log.error("Error setting value for field '{}': {}", fieldName, e.getMessage(), e);
    }
  }

  private static boolean isValidTopicNames(String[] topicNames) {
    Set<String> validTopicNames =
        Arrays.stream(ContentionEvent.values())
            .map(ContentionEvent::getTopicName)
            .collect(Collectors.toSet());

    return Arrays.stream(topicNames).allMatch(validTopicNames::contains);
  }
}
