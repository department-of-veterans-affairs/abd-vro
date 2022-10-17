package gov.va.vro.end2end;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class Dc7701Tests {
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void positive01() throws Exception {
    ObjectNode req = mapper.createObjectNode();
    req.put("claimSubmissionId", "7001");
    req.put("veteranIcn", "1012666073V986297");
    req.put("diagnosticCode", "7101");

    String actual =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", "test-key-01")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(10000))
            .build()
            .post()
            .uri("/full-health-data-assessment")
            .body(BodyInserters.fromValue(req.toString()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    InputStream stream = this.getClass().getResourceAsStream("/test-7701-01/assessment.json");
    String expected = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);





  }



}
