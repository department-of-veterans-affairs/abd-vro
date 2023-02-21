package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.end2end.util.PdfTextV2;
import gov.va.vro.end2end.util.UpdatesResponse;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Slf4j
public class VroV2Tests {

  private static final String BASE_URL = "http://localhost:8080/v2";
  private static final String EXAM_ORDERING_STATUS_URL = BASE_URL + "/examOrderingStatus";
  private static final String AUTOMATED_CLAIM_URL = BASE_URL + "/automatedClaim";

  private static final String UPDATES_URL = "http://localhost:8099/updates/";
  private static final String RECEIVED_FILES_URL = "http://localhost:8096/received-files/";

  private static final String JWT_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUxYjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00MWE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWwvYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05NmY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RestTemplate restTemplate = new RestTemplate();

  /*
   * This test checks the RequestBodyAdvice sanitizing logic for disallowed characters.
   * Eventually we should refactor this out into its own test suite with other endpoints
   * for any security-related HTTP tests.
   */
  @Test
  void testExamOrderingStatus_disallowedCharacters() {
    var request = getOrderingStatusDisallowedCharacters();
    var requestEntity = getEntity(request);
    try {
      restTemplate.postForEntity(EXAM_ORDERING_STATUS_URL, requestEntity, String.class);
      fail("Should have thrown exception");
    } catch (Exception e) {
      assertTrue("400 : \"{\"message\":\"Bad Request\"}\"".equals(e.getMessage()));
    }
  }

  @Test
  void testExamOrderingStatus_invalidRequest() {
    var request = getOrderingStatusInvalidRequest();
    var requestEntity = getEntity(request);
    try {
      restTemplate.postForEntity(EXAM_ORDERING_STATUS_URL, requestEntity, String.class);
      fail("Should have thrown exception");
    } catch (Exception e) {
      assertTrue(
          "400 : \"{\"message\":\"collectionId: Collection ID is required\\ncollectionStatus: Collection Status is required\"}\""
                  .equals(e.getMessage())
              || "400 : \"{\"message\":\"collectionStatus: Collection Status is required\\ncollectionId: Collection ID is required\"}\""
                  .equals(e.getMessage()));
    }
  }

  @Test
  void testExamOrderingStatus() {
    var request = getOrderingStatusValidRequest();
    var requestEntity = getEntity(request);
    var response =
        restTemplate.postForEntity(EXAM_ORDERING_STATUS_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals("Received Exam Order Status for collection Id 123.", masResponse.getMessage());
  }

  @SneakyThrows
  private boolean getFoundStatus(String claimId, String type) {
    for (int pollNumber = 0; pollNumber < 10; ++pollNumber) {
      Thread.sleep(10);
      String url = UPDATES_URL + claimId + "/" + type;
      var testResponse = restTemplate.getForEntity(url, UpdatesResponse.class);
      assertEquals(HttpStatus.OK, testResponse.getStatusCode());
      UpdatesResponse body = testResponse.getBody();
      if (body.isFound()) {
        log.info("{} is updated.", type);
        return true;
      } else {
        log.info("{} is not updated. Retrying...", type);
      }
    }
    return false;
  }

  /**
   * Runs a full end-to-end test for the collection id using mock services. Collection id used here
   * should be one of the preloaded ones in mock-mas-api amd the benefit claim id should one of the
   * ones in mock-bip-claims-api.
   */
  @SneakyThrows
  private void testAutomatedClaimFullPositive(String collectionId) {

    // Load the test case
    var path = String.format("test-mas/claim-%s-7101.json", collectionId);
    var content = resourceToString(path);

    // Extract claim id and file number and reset previous actions for those in mocks
    final MasAutomatedClaimRequest request =
        objectMapper.readValue(content, MasAutomatedClaimRequest.class);
    final String claimId = request.getClaimDetail().getBenefitClaimId();
    final String fileNumber = request.getVeteranIdentifiers().getVeteranFileId();
    log.info("Reset data in the mock servers.");
    restTemplate.delete(UPDATES_URL + claimId);
    restTemplate.delete(RECEIVED_FILES_URL + fileNumber);

    // Start automated claim
    var requestEntity = getEntity(content);
    var response =
        restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    String expectedMessage = String.format("Received Claim for collection Id %s.", collectionId);
    assertEquals(expectedMessage, masResponse.getMessage());

    // Wait until the evidence pdf is uploaded
    log.info("Wait until the evidence pdf is uploaded");
    boolean successUploading = false;
    for (int pollNumber = 0; pollNumber < 15; ++pollNumber) {
      Thread.sleep(20000);
      String url = RECEIVED_FILES_URL + fileNumber;
      try {
        ResponseEntity<byte[]> testResponse = restTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        PdfTextV2 pdfTextV2 = PdfTextV2.getInstance(testResponse.getBody());
        log.info("PDF text: {}", pdfTextV2.getPdfText());
        assertTrue(pdfTextV2.hasVeteranName(request.getFirstName(), request.getLastName()));
        successUploading = true;
        break;
      } catch (HttpStatusCodeException exception) {
        log.info("Did not find veteran {}. Retrying...", fileNumber);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }
    }

    // Verify evidence pdf is uploaded
    assertTrue(successUploading);
    // Verify contentions are updated (TODO: verify the actual update here)
    boolean contentionsFound = getFoundStatus(claimId, "contentions");
    assertTrue(contentionsFound);
    // Verify lifecycle status is updated (TODO: verify the actual update here)
    boolean lifecycleStatusFound = getFoundStatus(claimId, "lifecycle_status");
    assertTrue(lifecycleStatusFound);
  }

  @SneakyThrows
  @Test
  void testAutomatedClaim() {
    testAutomatedClaimFullPositive("350");
  }

  /** Tests if Bip Claim Api 404 for non-existent claim results in 400 on our end. */
  @Test
  @SneakyThrows
  void testAutomatedClaimNonExistentClaimId() {
    var path = "test-mas/claim-801-7101-nonexistent-claimid.json";
    var content = resourceToString(path);
    var requestEntity = getEntity(content);
    try {
      var response =
          restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
      fail("Collection 801 should have received 400.");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
  }

  @Test
  void testAutomatedClaim_outOfScope() {
    var path = "test-mas/claim-350-7101-outofscope.json";
    var content = resourceToString(path);
    String url = BASE_URL + "/automatedClaim";
    var requestEntity = getEntity(content);
    var response = restTemplate.postForEntity(url, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals(
        "Claim with [collection id = 350], [diagnostic code = 7101], and [disability action type = DECREASE] is not in scope.",
        masResponse.getMessage());
  }

  @Test
  void testAutomatedClaim_missingAnchor() {
    var path = "test-mas/claim-351-7101-noanchor.json";
    var content = resourceToString(path);
    String url = BASE_URL + "/automatedClaim";
    var requestEntity = getEntity(content);
    var response = restTemplate.postForEntity(url, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals(
        "Claim with [collection id = 351] does not qualify for automated processing because it is missing anchors.",
        masResponse.getMessage());
  }

  @SneakyThrows
  private String resourceToString(String path) {
    var io = this.getClass().getClassLoader().getResourceAsStream(path);
    try (Reader reader = new InputStreamReader(io)) {
      return FileCopyUtils.copyToString(reader);
    }
  }

  @SneakyThrows
  private String getOrderingStatusDisallowedCharacters() {
    return objectMapper.writeValueAsString(
        Map.of(
            "collectionId", "999",
            "collectionStatus",
                "http://localhost:8080/v1/fetch-claims/%00%255c%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/%252e%252e%255c/windows/system.ini\b\u007F\u0081\u0088/%00",
            "examOrderDateTime", "2018-11-04T17:45:61Z"));
  }

  @SneakyThrows
  private String getOrderingStatusInvalidRequest() {
    return objectMapper.writeValueAsString(Map.of("key1", "value1", "key2", "value2"));
  }

  @SneakyThrows
  private String getOrderingStatusValidRequest() {
    var payload =
        Map.of(
            "collectionId",
            "123",
            "collectionStatus",
            "DRAFT",
            "examOrderDateTime",
            "2022-12-08T17:45:59Z",
            "eventId",
            "None");
    return objectMapper.writeValueAsString(payload);
  }

  private HttpEntity<String> getEntity(String content) {
    return new HttpEntity<>(content, getHttpHeaders());
  }

  private static HttpHeaders getHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(JWT_TOKEN);
    return headers;
  }

  /**
   * This is a positive end to end set. Two set of blood pressures and medications come from Health
   * API and MAS collections. They are not really related. You can check this to see how the pdfs
   * look like with data from both sources.
   *
   * <p>After the run get the pdf from http://localhost:8094/9999375
   */
  @SneakyThrows
  @Test
  void testAutomatedClaimSufficientSeparate() {
    testAutomatedClaimFullPositive("375");
  }

  /**
   * This is an identical to testAutomatedClaimSufficientSeparate except it is a presumptive case.
   * The file number, collection id and claim numbers also differ.
   *
   * <p>After the run get the pdf from http://localhost:8094/9999376
   */
  @SneakyThrows
  @Test
  void testAutomatedClaimPresumptive() {
    testAutomatedClaimFullPositive("376");
  }
}
