package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class VroV2Tests {

  private static final String BASE_URL = "http://localhost:8080/v2";

  private static final String JWT_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RestTemplate restTemplate = new RestTemplate();

  @Test
  void testExamOrderingStatus_invalidRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(JWT_TOKEN);
    String request = getOrderingStatusInvalidRequest();
    HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);
    String url = BASE_URL + "/examOrderingStatus";
    try {
      restTemplate.postForEntity(url, requestEntity, String.class);
      fail("Should have thrown exception");
    } catch (Exception e) {
      assertEquals(
          "400 : \"{\"message\":\"collectionStatus: Collection Status is required\"}\"",
          e.getMessage());
    }
  }

  @Test
  void testExamOrderingStatus() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(JWT_TOKEN);
    String request = getOrderingStatusValidRequest();
    HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);
    String url = BASE_URL + "/examOrderingStatus";
    var response = restTemplate.postForEntity(url, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals("Received", masResponse.getMessage());
  }

  @SneakyThrows
  private String getOrderingStatusInvalidRequest() {
    Map<String, String> payload = new HashMap<>();
    payload.put("key1", "value1");
    payload.put("key2", "value2");
    return objectMapper.writeValueAsString(payload);
  }

  @SneakyThrows
  private String getOrderingStatusValidRequest() {
    Map<String, String> payload = new HashMap<>();
    payload.put("collectionId", "123");
    payload.put("collectionStatus", "DRAFT");
    payload.put("examOrderDateTime", "2022-12-08T17:45:61Z");
    payload.put("eventId", "None");
    return objectMapper.writeValueAsString(payload);
  }
}
