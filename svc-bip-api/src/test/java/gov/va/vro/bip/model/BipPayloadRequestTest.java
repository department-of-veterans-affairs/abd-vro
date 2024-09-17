package gov.va.vro.bip.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BipPayloadRequestTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Jacksonized
  @SuperBuilder
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Payload extends BipPayloadRequest {}

  @Nested
  class Deserialization {

    @Test
    void populatesExternalUserIdAndExternalKey() throws Exception {
      String json = "{\"externalUserId\":\"user123\",\"externalKey\":\"key123\"}";
      Payload deserializedRequest = objectMapper.readValue(json, Payload.class);
      assertEquals("user123", deserializedRequest.getExternalUserId());
      assertEquals("key123", deserializedRequest.getExternalKey());
    }

    @Test
    void doesNotPopulatesExternalUserIdAndExternalKey() throws Exception {
      String json = "{}";
      Payload deserializedRequest = objectMapper.readValue(json, Payload.class);
      assertNull(deserializedRequest.getExternalUserId());
      assertNull(deserializedRequest.getExternalKey());
    }

    @Test
    void ignoresUnknownProperties() {
      String json =
          "{\"externalUserId\":\"user123\",\"externalKey\":\"key123\",\"unknownProperty\":\"value\"}";

      Payload deserializedRequest =
          Assertions.assertDoesNotThrow(() -> objectMapper.readValue(json, Payload.class));

      assertEquals("user123", deserializedRequest.getExternalUserId());
      assertEquals("key123", deserializedRequest.getExternalKey());
    }
  }

  @Nested
  class Serialization {

    @Test
    void doesNotSerializesExternalUserIdOrUserKey() throws Exception {
      Payload request = Payload.builder().externalUserId("user123").externalKey("key123").build();

      String json = objectMapper.writeValueAsString(request);
      assertFalse(json.contains("externalUserId"));
      assertFalse(json.contains("externalKey"));
    }
  }
}
