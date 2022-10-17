package gov.va.vro.end2end;

import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.Duration;

@Slf4j
public class Dc7701Tests {
  /*
  WebClient client =
      WebClient.builder()
          .baseUrl("http://localhost:8080/v1")
          .defaultHeader("X-API-KEY", "test-key-01")
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build();
  */
  @Test
  public void positive01() {
    HealthDataAssessmentRequest req = new HealthDataAssessmentRequest();
    req.setClaimSubmissionId("7001");
    req.setVeteranIcn("1012666073V986297");
    req.setDiagnosticCode("7101");

    String result =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", "test-key-01")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(10000))
            .build()
            .post()
            .uri("/full-health-data-assessment")
            .body(BodyInserters.fromValue(req))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    log.info(result);
    /*
     */
    /*
    WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.post();

    WebClient.RequestBodySpec bodySpec = uriSpec.uri("/full-health-data-assessment");
    WebClient.RequestHeadersSpec headerSpec = bodySpec.body(BodyInserters.fromValue(req));

    WebClient.ResponseSpec responseSpec = headerSpec.retrieve();

    responseSpec.
    */
  }
}
