package gov.va.vro.end2end.util;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Getter
@Setter
public class RestHelper {
  private static final long DEFAULT_TIMEOUT = 15000;
  private static final String BASE_URL = "http://localhost:8080/v1";
  private static final String API_KEY = "X-API-KEY";
  private static final String ASSESSMENT_END_POINT = "/full-health-data-assessment";
  private static final String PDF_END_POINT = "/evidence-pdf";
  private String apiKey;

  private WebTestClient buildClient() {
    HttpClient client =
        HttpClient.create()
            .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(EpollChannelOption.TCP_KEEPIDLE, 60)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .option(EpollChannelOption.TCP_KEEPCNT, 8);

    return WebTestClient.bindToServer()
        .baseUrl(BASE_URL)
        .defaultHeader(API_KEY, apiKey)
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
