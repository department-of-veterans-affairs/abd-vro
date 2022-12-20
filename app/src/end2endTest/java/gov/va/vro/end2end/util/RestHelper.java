package gov.va.vro.end2end.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;

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
   * @return health assessment response
   * @throws Exception any error to fail the test
   */
  public String getAssessment(TestSetup setup) throws Exception {
    String req = setup.getAssessmentRequest();
    HttpHeaders headers = buildHeaders();
    HttpEntity<String> requestEntity = new HttpEntity<>(req, headers);
    String url = BASE_URL + ASSESSMENT_END_POINT;
    log.info("POST to {} with request: {}", url, requestEntity);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("Got response: {}", response);

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
    log.info("POST to {} with request: {}", url, requestEntity);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("Got response: {}", response);

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
    log.info("GET to {} with request: {}", url, requestEntity);
    ResponseEntity<byte[]> response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
    log.info("Got response of type: {}", response.getBody().getClass().getSimpleName());

    if(Boolean.parseBoolean(System.getenv("VRO_SAVE_PDF"))) {
      savePdfFile(response.getBody(), setup.getName() + "-" + setup.getContentDispositionFilename());
    }

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    HttpHeaders responseHeaders = response.getHeaders();
    log.info("Response headers: {}", responseHeaders);
    ContentDisposition actualCd = responseHeaders.getContentDisposition();
    String filename = actualCd.getFilename();
    Assertions.assertEquals(cd, filename);

    return response.getBody();
  }

  private void savePdfFile(byte[] pdfContents, String filename) {
    try (FileOutputStream outputStream = new FileOutputStream(filename)) {
      outputStream.write(pdfContents);
      log.info("Saved pdf to: {}", filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
