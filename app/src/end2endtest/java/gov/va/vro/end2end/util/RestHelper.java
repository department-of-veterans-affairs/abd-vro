package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.Duration;

@Getter
@Setter
public class RestHelper {
  static final private long DEFAULT_TIMEOUT = 15000;
  private String apiKey;

  public String getAssessment(TestSetup setup) throws Exception {
    String input = setup.getAssessmentInput();

    String result =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
            .build()
            .post()
            .uri("/full-health-data-assessment")
            .body(BodyInserters.fromValue(input))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return result;
  }

  public String generatePdf(TestSetup setup) throws Exception {
    String pdfGenInput = setup.getGeneratePdfInput();

    String result =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
            .build()
            .post()
            .uri("/evidence-pdf")
            .body(BodyInserters.fromValue(pdfGenInput))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return result;
  }

  public byte[] getPdf(TestSetup setup) {
    String cd = setup.getContentDisposition();

    byte[] result =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/evidence-pdf/7001")
                .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("Content-Disposition", cd)
            .expectBody()
            .returnResult()
            .getResponseBody();

    return result;
  }
}
