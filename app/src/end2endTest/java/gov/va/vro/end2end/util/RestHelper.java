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
  private static final long DEFAULT_TIMEOUT = 15000;
  private String apiKey;

  private WebTestClient buildClient() {
    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:8080/v1")
        .defaultHeader("X-API-KEY", apiKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
        .build();
  }

  /**
   * Generates the health assessment.
   *
   * @param setup test settings
   * @return health assessment response
   * @throws Exception any error to fail the test
   */
  public String getAssessment(TestSetup setup) throws Exception {
    String req = setup.getAssessmentRequest();

    String result =
        buildClient()
            .post()
            .uri("/full-health-data-assessment")
            .body(BodyInserters.fromValue(req))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return result;
  }

  /**
   * Generates the evidence pdf.
   *
   * @param setup test settings
   * @return generate pdf response
   * @throws Exception any error to fail the test
   */
  public String generatePdf(TestSetup setup) throws Exception {
    String req = setup.getGeneratePdfRequest();

    String result =
        buildClient()
            .post()
            .uri("/evidence-pdf")
            .body(BodyInserters.fromValue(req))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return result;
  }

  /**
   * Gets the evidence pdf as a byte array.
   *
   * @param setup test settings
   * @return evidence pdf as byte array
   */
  public byte[] getPdf(TestSetup setup) {
    String cd = setup.getContentDisposition();
    String claimSubmissionId = setup.getClaimSubmissionId();

    byte[] result =
        buildClient()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/evidence-pdf/" + claimSubmissionId).build())
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
