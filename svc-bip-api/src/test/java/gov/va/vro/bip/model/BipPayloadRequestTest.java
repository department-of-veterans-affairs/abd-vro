package gov.va.vro.bip.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BipPayloadRequestTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Nested
  class Deserialization {

    @Test
    void populatesExternalUserIdAndExternalKey() throws Exception {
      String json = "{\"externalUserId\":\"user123\",\"externalKey\":\"key123\"}";
      BipPayloadRequest deserializedRequest = objectMapper.readValue(json, BipPayloadRequest.class);
      assertEquals("user123", deserializedRequest.getExternalUserId());
      assertEquals("key123", deserializedRequest.getExternalKey());
    }

    @Test
    void doesNotPopulatesExternalUserIdAndExternalKey() throws Exception {
      String json = "{}";
      BipPayloadRequest deserializedRequest = objectMapper.readValue(json, BipPayloadRequest.class);
      assertNull(deserializedRequest.getExternalUserId());
      assertNull(deserializedRequest.getExternalKey());
    }

    @Test
    void ignoresUnknownProperties() {
      String json =
          "{\"externalUserId\":\"user123\",\"externalKey\":\"key123\",\"unknownProperty\":\"value\"}";

      BipPayloadRequest deserializedRequest =
          Assertions.assertDoesNotThrow(
              () -> objectMapper.readValue(json, BipPayloadRequest.class));

      assertEquals("user123", deserializedRequest.getExternalUserId());
      assertEquals("key123", deserializedRequest.getExternalKey());
    }
  }

  @Nested
  class Serialization {

    @Test
    void doesNotSerializesExternalUserIdOrUserKey() throws Exception {
      BipPayloadRequest request =
          BipPayloadRequest.builder().externalUserId("user123").externalKey("key123").build();

      String json = objectMapper.writeValueAsString(request);
      assertFalse(json.contains("externalUserId"));
      assertFalse(json.contains("externalKey"));
    }
  }
}
