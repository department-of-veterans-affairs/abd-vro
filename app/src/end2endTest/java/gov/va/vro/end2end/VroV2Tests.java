package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.end2end.util.AutomatedClaimTestSpec;
import gov.va.vro.end2end.util.OrderExamCheckResponse;
import gov.va.vro.end2end.util.PdfTextV2;
import gov.va.vro.end2end.util.UpdatesResponse;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.claimmetrics.AssessmentInfo;
import gov.va.vro.model.claimmetrics.ContentionInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

@Slf4j
public class VroV2Tests {

  private static final String BASE_URL = "http://localhost:8080/v2";
  private static final String EXAM_ORDERING_STATUS_URL = BASE_URL + "/examOrderingStatus";
  private static final String AUTOMATED_CLAIM_URL = BASE_URL + "/automatedClaim";
  private static final String CLAIM_INFO_URL = BASE_URL + "/claim-info/";
  private static final String UPDATES_URL = "http://localhost:8099/updates/";
  private static final String RECEIVED_FILES_URL = "http://localhost:8096/received-files/";
  private static final String ORDER_EXAM_URL = "http://localhost:9001/checkExamOrdered/";
  private static final String SLACK_URL = "http://localhost:9008/slack-messages/";
  private static final String CONTENTIONS_URL = "http://localhost:8099/claims/%s/contentions";

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
    var requestEntity = getBearerAuthEntity(request);
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
    var requestEntity = getBearerAuthEntity(request);
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
    var requestEntity = getBearerAuthEntity(request);
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

  @SneakyThrows
  private List<ClaimContention> getUpdatedContentions(String claimId) {
    for (int pollNumber = 0; pollNumber < 10; ++pollNumber) {
      Thread.sleep(10);
      String url = UPDATES_URL + claimId + "/" + "contentions";
      var testResponse = restTemplate.getForEntity(url, UpdatesResponse.class);
      assertEquals(HttpStatus.OK, testResponse.getStatusCode());
      UpdatesResponse body = testResponse.getBody();
      if (body.isFound()) {
        log.info("Claim {} contentions are updated.", claimId);
        return body.getContentions();
      } else {
        log.info("Claim {} contentions are not updated. Retrying...", claimId);
      }
    }
    return null;
  }

  @SneakyThrows
  private MasAutomatedClaimRequest startAutomatedClaim(AutomatedClaimTestSpec spec) {
    final String collectionId = spec.getCollectionId();

    // Load the test case
    var path = spec.getPayloadPath();
    var content = resourceToString(path);

    // Extract claim id and file number and reset previous actions for those in mocks
    final MasAutomatedClaimRequest request =
        objectMapper.readValue(content, MasAutomatedClaimRequest.class);
    final String claimId = request.getClaimDetail().getBenefitClaimId();
    final String fileNumber = request.getVeteranIdentifiers().getVeteranFileId();
    log.info("Reset data in the mock servers.");
    restTemplate.delete(UPDATES_URL + claimId);
    restTemplate.delete(RECEIVED_FILES_URL + fileNumber);
    restTemplate.delete(ORDER_EXAM_URL + collectionId);
    if (spec.isCheckSlack()) {
      restTemplate.delete(SLACK_URL + collectionId);
    }

    // Start automated claim
    var requestEntity = getBearerAuthEntity(content);
    var response =
        restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals(spec.getExpectedMessage(), masResponse.getMessage());
    return request;
  }

  @SneakyThrows
  private MasAutomatedClaimRequest startAutomatedClaim(String collectionId) {
    AutomatedClaimTestSpec spec = new AutomatedClaimTestSpec();
    spec.setCollectionId(collectionId);
    spec.setPayloadPath(String.format("test-mas/claim-%s-7101.json", collectionId));
    spec.setExpectedMessage(String.format("Received Claim for collection Id %s.", collectionId));
    return startAutomatedClaim(spec);
  }

  @SneakyThrows
  private void testPDFUpload(MasAutomatedClaimRequest request) {
    // Wait until the evidence pdf is uploaded
    final String fileNumber = request.getVeteranIdentifiers().getVeteranFileId();
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
  }

  @SneakyThrows
  public void testExamOrdered(String collectionId, boolean expectedExamOrder) {
    boolean successOrdering = false;
    if (expectedExamOrder) {
      String url = ORDER_EXAM_URL + collectionId;
      log.info("Wait for examOrder code to execute.");
      for (int pollNumber = 0; pollNumber < 15; ++pollNumber) {
        Thread.sleep(20000);
        var testResponse = restTemplate.getForEntity(url, OrderExamCheckResponse.class);
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        boolean examOrdered = testResponse.getBody().isOrdered();
        if (examOrdered) {
          log.info("{} had exam ordered", collectionId);
        } else {
          log.info("{} did NOT have exam ordered", collectionId);
        }
        if (examOrdered) {
          successOrdering = true;
          break;
        } else {
          log.info(
              "Exam not ordered yet for collection {}. Waiting and rechecking...", collectionId);
        }
      }
    } else {
      log.info("Negative test case for exam ordering TBD via database check. Skipping polling");
    }
    assertEquals(successOrdering, expectedExamOrder);
  }

  @SneakyThrows
  private void testClaimSufficientStatus(String collectionId, Boolean expectedSufficientValue) {

    String url = CLAIM_INFO_URL + collectionId;
    AssessmentInfo foundAssessment = null;
    HttpEntity<Void> requestEntity = getTokenAuthHeaders();

    log.info("Waiting for claim processing to finish and assessment database results");
    for (int pollNumber = 0; pollNumber < 15; ++pollNumber) {
      Thread.sleep(20000);
      try {
        ResponseEntity<ClaimInfoResponse> testResponse =
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, ClaimInfoResponse.class);
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        ClaimInfoResponse cir = testResponse.getBody();
        assertNotNull(cir, "Claim Info Response was null, cannot continue");
        List<ContentionInfo> contentionList = cir.getContentions();
        if (contentionList.size() == 1) {
          List<AssessmentInfo> assessmentList = contentionList.get(0).getAssessments();
          // If assessment list size is zero, we may not be finished processing, and should try
          // again.
          if (assessmentList.size() == 1) {
            foundAssessment = assessmentList.get(0);
            break;
          } else if (assessmentList.size() > 1) {
            Assertions.fail(
                "CollectionId "
                    + collectionId
                    + " came back with more than one assessment result. Cannot determine which one to check");
          }
        } else if (contentionList.size() > 1) {
          Assertions.fail(
              "CollectionId "
                  + collectionId
                  + " came back with more than one contention. Cannot determine which one to check");
        }
      } catch (HttpStatusCodeException exception) {
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        log.info(
            "Did not find asessment result for collection id {} with message .. retrying",
            collectionId);
      }
    }
    assertNotNull(foundAssessment);
    assertEquals(expectedSufficientValue, foundAssessment.getSufficientEvidenceFlag());
  }

  /**
   * Runs a full end-to-end test for the collection id using mock services. Collection id used here
   * should be one of the preloaded ones in mock-mas-api amd the benefit claim id should one of the
   * ones in mock-bip-claims-api.
   */
  @SneakyThrows
  private void testAutomatedClaimFullPositive(String collectionId, boolean expectedStatusUpdate) {

    MasAutomatedClaimRequest request = startAutomatedClaim(collectionId);
    final String claimId = request.getClaimDetail().getBenefitClaimId();

    if (!expectedStatusUpdate) {
      return;
    }

    testPDFUpload(request);

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
    testAutomatedClaimFullPositive("350", false);
  }

  /** Tests if Bip Claim Api 404 for non-existent claim results in 400 on our end. */
  @Test
  @SneakyThrows
  void testAutomatedClaimNonExistentClaimId() {
    var path = "test-mas/claim-801-7101-nonexistent-claimid.json";
    var content = resourceToString(path);
    var requestEntity = getBearerAuthEntity(content);
    try {
      var response =
          restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
      fail("Collection 801 should have received 400.");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
  }

  @SneakyThrows
  private boolean testOffRampSlackMessage(String collectionId) {
    for (int pollNumber = 0; pollNumber < 15; ++pollNumber) {
      Thread.sleep(5000);
      String url = SLACK_URL + collectionId;
      try {
        ResponseEntity<String> testResponse = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        return true;
      } catch (HttpStatusCodeException exception) {
        log.info("Did not find slack message for {}. Retrying...", collectionId);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }
    }
    return false;
  }

  private void testSpecialIssueRdr1Removed(String claimId) {
    List<ClaimContention> contentions = getUpdatedContentions(claimId);
    assertNotNull(contentions, "Contentions are not updated to remove special issue.");
    log.info("Claim {} contentions have been updated.", claimId);

    // Only have single issue claims for now
    assertEquals(1, contentions.size());
    ClaimContention contention = contentions.get(0);
    List<String> specialIssueCodes = contention.getSpecialIssueCodes();
    assertNotNull(specialIssueCodes);
    for (String specialIssueCode : specialIssueCodes) {
      assertNotEquals("RDR1", specialIssueCode, "RDR1 should have been removed");
    }
    log.info("rdr1 is removed for {}", claimId);
  }

  private void testAutomatedClaimPreCamelOffRamp(AutomatedClaimTestSpec spec) {
    MasAutomatedClaimRequest request = startAutomatedClaim(spec);

    boolean slackResult = testOffRampSlackMessage(spec.getCollectionId());
    assertTrue(slackResult, "No or unexpected slack messages received by slack server");

    final String claimId = request.getClaimDetail().getBenefitClaimId();
    testSpecialIssueRdr1Removed(claimId);
  }

  /**
   * Out of scope test case because of disability action type. Rest response message,
   * Slack message and removal of RDR1 special issue are verified.
   */
  @Test
  void testAutomatedClaimOutOfScope() {
    AutomatedClaimTestSpec spec = new AutomatedClaimTestSpec("10");
    spec.setPayloadPath("test-mas/claim-10-7101-outofscope.json");
    spec.setExpectedMessage(
        "Claim with [collection id = 10], [diagnostic code = 7101], and [disability action type = DECREASE] is not in scope.");
    spec.setCheckSlack(true);

    testAutomatedClaimPreCamelOffRamp(spec);
  }

  /**
   * Missing anchor test case because of wrong temporary jurisdiction station. Rest response
   * message, Slack message and removal of RDR1 special issue are verified.
   */
  @Test
  void testAutomatedClaimMissingAnchor() {
    AutomatedClaimTestSpec spec = new AutomatedClaimTestSpec("20");
    spec.setPayloadPath("test-mas/claim-20-7101-noanchor.json");
    spec.setExpectedMessage(
        "Claim with [collection id = 20] does not qualify for automated processing because it is missing anchors.");
    spec.setCheckSlack(true);

    testAutomatedClaimPreCamelOffRamp(spec);
  }

  // Test Case that ensures that exam order *is* callled
  // The data underlying follows the NEW claim, one relevant condition, not enough information path.
  @Test
  void testAutomatedClaim_orderExamNewClaim() {
    MasAutomatedClaimRequest request = startAutomatedClaim("377");
    testExamOrdered("377", true);
    testPDFUpload(request);
  }

  // Test case that ensures the exam order *is* callled
  // The data underlying follows the "increase" claim path where not enough blood pressure readings
  // exist.
  @Test
  void testAutomatedClaim_orderExamIncreaseClaim() {
    MasAutomatedClaimRequest request = startAutomatedClaim("378");
    testExamOrdered("378", true);
    testPDFUpload(request);
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

  // Authorization for Claim Info e2e
  private HttpEntity<Void> getTokenAuthHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Key", "test-key-01");
    return new HttpEntity<>(headers);
  }

  private HttpEntity<String> getBearerAuthEntity(String content) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(JWT_TOKEN);
    return new HttpEntity<>(content, headers);
  }

  /**
   * This is a positive end to end set. Two set of blood pressures and medications come from Health
   * API and MAS collections. They are not really related. You can check this to see how the pdfs
   * look like with data from both sources.
   *
   * <p>After the run get the pdf from http://localhost:8096/recieved-files/9999375
   */
  @SneakyThrows
  @Test
  void testAutomatedClaimSufficientSeparate() {
    testAutomatedClaimFullPositive("375", true);
  }

  /**
   * This is an identical to testAutomatedClaimSufficientSeparate except it is a presumptive case.
   * The file number, collection id and claim numbers also differ.
   *
   * <p>After the run get the pdf from http://localhost:8096/recieved-files/9999376
   */
  @SneakyThrows
  @Test
  void testAutomatedClaimPresumptive() {
    testAutomatedClaimFullPositive("376", true);
  }

  @SneakyThrows
  @Test
  void testIncompleteBloodPressures() {
    testAutomatedClaimFullPositive("380", true);
  }

  @SneakyThrows
  @Test
  void testAutomatedSufficiencyIsNull() {
    // Offramp claims do not go through pdf process per VRO workflow diagram.
    testAutomatedClaimFullPositive("500", false);
    testClaimSufficientStatus("500", null);
  }
}
