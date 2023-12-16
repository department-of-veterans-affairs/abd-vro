package gov.va.vro.services.bie.utils;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.annotation.Ignore;
import gov.va.vro.model.biekafka.annotation.TargetEvents;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class BieMessageUtils {
  public static BieMessagePayload processBieMessagePayloadFields(ContentionEvent contentionEvent, GenericRecord genericRecord) {
    BieMessagePayload payload = BieMessagePayload.builder().status(200).build();

    for (Field field : BieMessagePayload.class.getDeclaredFields()) {
      String fieldName = field.getName();

      // Skip the field if it has @Ignore annotation or is null
      if (field.isAnnotationPresent(Ignore.class)) {
        continue;
      }

      if (field.isAnnotationPresent(TargetEvents.class)) {
        String[] annotationValues = field.getAnnotation(TargetEvents.class).value();

        if (!isValidTopicNames(annotationValues)) {
          throw new IllegalArgumentException("Invalid topic names in annotation for field: " + fieldName);
        }

        boolean matchedTopicName = Arrays.stream(annotationValues).anyMatch(v -> v.equals(contentionEvent.getTopicName()));

        if(matchedTopicName) {
          invokeSetterMethod(payload, field, genericRecord);
        }
      } else {
        invokeSetterMethod(payload, field, genericRecord);
      }
    }

    return payload;
  }

  private static void invokeSetterMethod(BieMessagePayload payload, Field field, GenericRecord genericRecord) {
    String fieldName = field.getName();
    String capitalizedFieldName = StringUtils.capitalize(fieldName);
    Object value = genericRecord.get(capitalizedFieldName);
    String setterMethodName = "set" + capitalizedFieldName;

    // if GenericRecord doesn't have the value, we do not process further
    if(value == null) return;

    try {
      Method setterMethod = BieMessagePayload.class.getMethod(setterMethodName, field.getType());

      if (field.getType().isAssignableFrom(value.getClass())) {
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
    Set<String> validTopicNames = Arrays.stream(ContentionEvent.values())
            .map(ContentionEvent::getTopicName)
            .collect(Collectors.toSet());

    return Arrays.stream(topicNames).allMatch(validTopicNames::contains);
  }
}
