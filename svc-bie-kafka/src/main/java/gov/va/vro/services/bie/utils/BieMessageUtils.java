package gov.va.vro.services.bie.utils;

import gov.va.vro.model.biekafka.BieMessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BieMessageUtils {
  private static final Set<String> IGNORED_FIELDS = new HashSet<>();

  static {
    // Add the names of fields to be ignored
    IGNORED_FIELDS.add("status");
    IGNORED_FIELDS.add("statusMessage");
    IGNORED_FIELDS.add("eventType");
    IGNORED_FIELDS.add("notifiedAt");
  }

  public static BieMessagePayload processBieMessagePayloadFields(GenericRecord genericRecord) {
    BieMessagePayload payload = BieMessagePayload.builder().status(200).build();

    for (Field field : BieMessagePayload.class.getDeclaredFields()) {
      String fieldName = field.getName();

      // Skip the field if it's in the ignored list
      if (IGNORED_FIELDS.contains(fieldName)) {
        continue;
      }

      String capitalizedFieldName = StringUtils.capitalize(fieldName);
      Object value = genericRecord.get(capitalizedFieldName);

      if (value == null) {
        continue; // Skip setting the field if the value is null
      }

      try {
        String setterMethodName = "set" + capitalizedFieldName;
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

    return payload;
  }
}
