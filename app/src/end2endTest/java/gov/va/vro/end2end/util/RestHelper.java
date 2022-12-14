package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
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
   * @return health assessment response
   * @throws Exception any error to fail the test
   */
  public String getAssessment(TestSetup setup) throws Exception {
    String req = setup.getAssessmentRequest();
    HttpHeaders headers = buildHeaders();
    HttpEntity<String> requestEntity = new HttpEntity<>(req, headers);
    String url = BASE_URL + ASSESSMENT_END_POINT;
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

    return response.getBody();
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
    HttpHeaders headers = buildHeaders();
    HttpEntity<String> requestEntity = new HttpEntity<>(req, headers);
    String url = BASE_URL + PDF_END_POINT;
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    return response.getBody();
  }

  /**
   * Gets the evidence pdf as a byte array.
   *
   * @param setup test settings
   * @return evidence pdf as byte array
   */
  public byte[] getPdf(TestSetup setup) {
    String cd = setup.getContentDispositionFilename();
    String claimSubmissionId = setup.getClaimSubmissionId();

    HttpHeaders headers = buildHeaders();
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    String url = BASE_URL + PDF_END_POINT + "/" + claimSubmissionId;
    ResponseEntity<byte[]> response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    HttpHeaders responseHeaders = response.getHeaders();
    ContentDisposition actualCd = responseHeaders.getContentDisposition();
    String filename = actualCd.getFilename();
    Assertions.assertEquals(cd, filename);

    return response.getBody();
  }
}
