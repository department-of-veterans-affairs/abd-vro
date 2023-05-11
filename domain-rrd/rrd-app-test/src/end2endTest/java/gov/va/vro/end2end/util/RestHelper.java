package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
@Slf4j
public class RestHelper {
  private static final String BASE_URL = "http://localhost:8080/v1";
  private static final String API_KEY = "X-API-KEY";
  private static final String ASSESSMENT_END_POINT = "/full-health-data-assessment";
  private static final String PDF_END_POINT = "/evidence-pdf";

  private RestTemplate restTemplate = new RestTemplate();

  private String apiKey;

  private HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(API_KEY, apiKey);
    return headers;
  }

  /**
   * Generates the health assessment.
   *
   * @param setup test settings
   * @return ResponseEntity containing health assessment
   * @throws Exception any error to fail the test
   */
  public ResponseEntity<String> getAssessment(TestSetup setup) {
    HttpEntity<String> requestEntity =
        new HttpEntity<>(setup.getAssessmentRequest(), buildHeaders());
    String url = BASE_URL + ASSESSMENT_END_POINT;
    log.info("POST to {} with request: {}", url, requestEntity);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("Got response: {}", response);
    return response;
  }

  /**
   * Generates the evidence pdf.
   *
   * @param setup test settings
   * @return ResponseEntity containing generate pdf metadata
   * @throws Exception any error to fail the test
   */
  public ResponseEntity<String> generatePdf(TestSetup setup) {
    HttpEntity<String> requestEntity =
        new HttpEntity<>(setup.getGeneratePdfRequest(), buildHeaders());
    String url = BASE_URL + PDF_END_POINT;
    log.info("POST to {} with request: {}", url, requestEntity);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("Got response: {}", response);
    return response;
  }

  /**
   * Gets the evidence pdf as a byte array.
   *
   * @param setup test settings
   * @return ResponseEntity containing evidence pdf as byte array
   */
  public ResponseEntity<byte[]> getPdf(TestSetup setup) {
    String claimSubmissionId = setup.getClaimSubmissionId();

    HttpHeaders headers = buildHeaders();
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    String url = BASE_URL + PDF_END_POINT + "/" + claimSubmissionId;
    log.info("GET to {} with request: {}", url, requestEntity);
    ResponseEntity<byte[]> response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
    log.info("Got response of type: {}", response.getBody().getClass().getSimpleName());
    return response;
  }
}
