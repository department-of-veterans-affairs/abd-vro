package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.Reader;

public class MasIntegrationTests {

  private static final String BASE_URL = "http://localhost:8080/vx";
  private static final String AUTOMATED_CLAIM_URL = BASE_URL + "/testAutomatedClaim";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RestTemplate restTemplate = new RestTemplate();

  @Test
  void testAutomatedClaim() {
    var path = "test-mas/claim-350-7101.json";
    var content = resourceToString(path);
    var requestEntity = new HttpEntity<>(content, getHttpHeaders());
    var response =
        restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals("Received", masResponse.getMessage());
  }

  // TODO: combine
  @SneakyThrows
  private String resourceToString(String path) {
    var io = this.getClass().getClassLoader().getResourceAsStream(path);
    try (Reader reader = new InputStreamReader(io)) {
      return FileCopyUtils.copyToString(reader);
    }
  }

  private static HttpHeaders getHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
