package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.Duration;

@Getter
@Setter
public class RestHelper {
  private static final String BASE_URL = "http://localhost:8080/v1";
  private static final String API_KEY = "X-API-KEY";
  private static final String ASSESSMENT_END_POINT = "/full-health-data-assessment";
  private static final String PDF_END_POINT = "/evidence-pdf";
  private static final long responseTimeout;

  static {
    String rtEnv = SystemUtils.getEnvironmentVariable("VRO_E2E_RESPONSE_TIMEOUT", "20000");
    responseTimeout = Long.parseLong(rtEnv);
  }
  private String apiKey;

  private WebTestClient buildClient() {
    return WebTestClient.bindToServer()
        .baseUrl(BASE_URL)
        .defaultHeader(API_KEY, apiKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .responseTimeout(Duration.ofMillis(responseTimeout))
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
            .uri(ASSESSMENT_END_POINT)
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
            .uri(PDF_END_POINT)
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
            .uri(uriBuilder -> uriBuilder.path(PDF_END_POINT + "/" + claimSubmissionId).build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals(HttpHeaders.CONTENT_DISPOSITION, cd)
            .expectBody()
            .returnResult()
            .getResponseBody();

    return result;
  }
}
