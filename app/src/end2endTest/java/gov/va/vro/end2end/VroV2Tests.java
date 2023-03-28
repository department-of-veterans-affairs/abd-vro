package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.end2end.util.AutomatedClaimTestSpec;
import gov.va.vro.end2end.util.ContentionUpdatesResponse;
import gov.va.vro.end2end.util.LifecycleUpdatesResponse;
import gov.va.vro.end2end.util.OrderExamCheckResponse;
import gov.va.vro.end2end.util.PdfTextV2;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.claimmetrics.AssessmentInfo;
import gov.va.vro.model.claimmetrics.ContentionInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.mas.VeteranIdentifiers;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class VroV2Tests {

  private static final String BASE_URL = "http://localhost:8080/v2";
  private static final String EXAM_ORDERING_STATUS_URL = BASE_URL + "/examOrderingStatus";
  private static final String AUTOMATED_CLAIM_URL = BASE_URL + "/automatedClaim";
  private static final String CLAIM_INFO_URL = BASE_URL + "/claim-info/";
  private static final String EXAM_ORDER_INFO_URL = BASE_URL + "/exam-order-info";
  private static final String UPDATES_URL = "http://localhost:8099/updates/";
  private static final String RECEIVED_FILES_URL = "http://localhost:8096/received-files/";
  private static final String ORDER_EXAM_URL = "http://localhost:9001/checkExamOrdered/";
  private static final String SLACK_URL = "http://localhost:9008/slack-messages/";

  private static final String JWT_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImMwOTI5NTJlLTM4ZDYtNDNjNi05MzBlLWZmOTNiYTUx"
          + "YjA4ZiJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTY0MTA2Nzk0OSwianRpIjoiNzEwOTAyMGEtMzlkOS00M"
          + "WE4LThlNzgtNTllZjAwYTlkNDJlIiwiaXNzIjoiaHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3YvaW50ZXJuYWw"
          + "vYXV0aC92Mi92YWxpZGF0aW9uIiwiYXVkIjoibWFzX2RldiIsInN1YiI6IjhjNDkyY2NmLTk0OGYtNDQ1Zi05N"
          + "mY4LTMxZTdmODU5MDlkMiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1hc19kZXYiLCJzY29wZSI6Im9wZW5pZCB"
          + "2cm9fbWFzIiwiY2xpZW50SWQiOiJtYXNfZGV2In0.Qb41CR1JIGGRlryi-XVtqyeNW73cU1YeBVqs9Bps3TA";

  private static final String MAS_ORDER_NOTIFY_STATUS = "VRONOTIFED";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RestTemplate restTemplate = new RestTemplate();

  @Test
  @SneakyThrows
  void testUniqueAutomatedClaimPayloads() {
    var resource = this.getClass().getClassLoader().getResource("test-mas");
    List<File> files =
        Files.walk(Paths.get(resource.toURI()))
            .filter(Files::isRegularFile)
            .filter(p -> p.getFileName().toString().endsWith(".json"))
            .map(x -> x.toFile())
            .collect(Collectors.toList());

    Set<Integer> collectionIds = new HashSet<>();
    Set<String> icns = new HashSet<>();
    Set<String> claimIds = new HashSet<>();
    Set<String> fileids = new HashSet<>();
    for (File file : files) {
      String content = Files.readString(file.toPath());
      var request = objectMapper.readValue(content, MasAutomatedClaimRequest.class);
      Integer collectionId = request.getCollectionId();
      assertNotNull(collectionId);
      assertFalse(
          collectionIds.contains(collectionId),
          String.format("collection id {} is not unique", collectionId));
      collectionIds.add(collectionId);

      VeteranIdentifiers identifiers = request.getVeteranIdentifiers();
      assertNotNull(identifiers);

      String icn = identifiers.getIcn();
      assertNotNull(icn);
      assertFalse(icns.contains(icn), String.format("icn %s is not unique", icn));
      icns.add(icn);

      String fileId = identifiers.getVeteranFileId();
      assertNotNull(fileId);
      assertFalse(fileids.contains(fileId), String.format("file id %s is not unique", fileId));
      fileids.add(fileId);

      var claimDetail = request.getClaimDetail();
      assertNotNull(claimDetail);
      String claimId = claimDetail.getBenefitClaimId();
      assertNotNull(claimId);
      assertFalse(claimIds.contains(claimId), String.format("claim id %s is not unique", claimId));
      claimIds.add(claimId);
    }
  }

  /*
   * This test checks the RequestBodyAdvice sanitizing logic for disallowed characters.
   * Eventually we should refactor this out into its own test suite with other endpoints
   * for any security-related HTTP tests.
   */
  @Test
  void testExamOrderingStatusDisallowedCharacters() {
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
  void testExamOrderingStatusInvalidRequest() {
    var request = getOrderingStatusInvalidRequest();
    var requestEntity = getBearerAuthEntity(request);
    try {
      restTemplate.postForEntity(EXAM_ORDERING_STATUS_URL, requestEntity, String.class);
      fail("Should have thrown exception");
    } catch (Exception e) {
      assertTrue(
          ("400 : \"{\"message\":\"collectionId: Collection ID is required\\ncollectionStatus:"
                      + " Collection Status is required\"}\"")
                  .equals(e.getMessage())
              || ("400 : \"{\"message\":\"collectionStatus: Collection Status is required\\"
                      + "ncollectionId: Collection ID is required\"}\"")
                  .equals(e.getMessage()));
    }
  }

  private void testExamOrderingStatus(String collectionId) {
    var request = getOrderingStatusValidRequest(collectionId);
    var requestEntity = getBearerAuthEntity(request);
    var response =
        restTemplate.postForEntity(EXAM_ORDERING_STATUS_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    String expectedMessage = "Received Exam Order Status for collection Id " + collectionId + ".";
    assertEquals(expectedMessage, masResponse.getMessage());
  }

  /**
   * This returns the lifecycle status. Lifecycle status is updated before the contention update so
   * this method can directly returns without polling.
   *
   * @param claimId claim identifier
   * @return the lifestyle status found
   */
  @SneakyThrows
  private String getUpdatedLifecycleStatus(String claimId) {
    String url = UPDATES_URL + claimId + "/" + "lifecycle_status";
    var testResponse = restTemplate.getForEntity(url, LifecycleUpdatesResponse.class);
    assertEquals(HttpStatus.OK, testResponse.getStatusCode());
    LifecycleUpdatesResponse body = testResponse.getBody();
    // Do not check isFound for now. We now do not update when necessary
    return body.getStatus();
  }

  /**
   * Contentions will always be updated since we check existence of RDR1 (anchor) before processing
   * begins and at least RDR1 will need to be removed.
   *
   * @param claimId claim identifier
   * @return all the contentions
   */
  @SneakyThrows
  private List<ClaimContention> getUpdatedContentions(String claimId) {
    for (int pollNumber = 0; pollNumber < 20; ++pollNumber) {
      String url = UPDATES_URL + claimId + "/" + "contentions";
      var testResponse = restTemplate.getForEntity(url, ContentionUpdatesResponse.class);
      assertEquals(HttpStatus.OK, testResponse.getStatusCode());
      ContentionUpdatesResponse body = testResponse.getBody();
      if (body.isFound()) {
        log.info("Claim {} contentions are updated.", claimId);
        return body.getContentions();
      } else {
        log.info("Claim {} contentions are not updated. Retrying...", claimId);
      }
      Thread.sleep(5);
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
    restTemplate.delete(SLACK_URL + collectionId);

    // Start automated claim
    var requestEntity = getBearerAuthEntity(content);
    var response =
        restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    var masResponse = response.getBody();
    assertEquals(spec.getExpectedMessage(), masResponse.getMessage());
    return request;
  }

  private MasAutomatedClaimRequest startAutomatedClaim(String collectionId) {
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    return startAutomatedClaim(spec);
  }

  private AutomatedClaimTestSpec specFor200(String collectionId) {
    AutomatedClaimTestSpec spec = new AutomatedClaimTestSpec();
    spec.setCollectionId(collectionId);
    spec.setPayloadPath(String.format("test-mas/claim-%s-7101.json", collectionId));
    spec.setExpectedMessage(String.format("Received Claim for collection Id %s.", collectionId));
    return spec;
  }

  @SneakyThrows
  private void testPdfUpload(MasAutomatedClaimRequest request) {
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

  /**
   * Test the exam ordered endpoint.
   *
   * @param collectionId collection ID
   */
  @SneakyThrows
  private void testExamOrdered(String collectionId) {
    boolean successOrdering = false;
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
        log.info("Exam not ordered yet for collection {}. Waiting and rechecking...", collectionId);
      }
    }
    assertTrue(successOrdering, "Exam is not ordered");
  }

  private ClaimInfoResponse getClaimInfoForCollection(String collectionId) {
    try {
      String url = CLAIM_INFO_URL + collectionId;
      HttpEntity<Void> requestEntity = getTokenAuthHeaders();
      ResponseEntity<ClaimInfoResponse> response =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, ClaimInfoResponse.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      ClaimInfoResponse body = response.getBody();
      assertNotNull(body, "Claim Info Response was null, cannot continue");
      return body;
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      return null;
    }
  }

  @SneakyThrows
  private void testClaimSufficientStatus(String collectionId, Boolean expectedSufficientValue) {
    AssessmentInfo foundAssessment = null;

    log.info("Waiting for claim processing to finish and assessment database results");
    for (int pollNumber = 0; pollNumber < 60; ++pollNumber) {
      ClaimInfoResponse cir = getClaimInfoForCollection(collectionId);
      if (cir == null) {
        log.info(
            "Did not find assessment result for collection id {} with message .. retrying",
            collectionId);
        Thread.sleep(5000);
        continue;
      }
      List<ContentionInfo> contentionList = cir.getContentions();
      // Just take the 0'th of now. If you run multiple times all should be same
      assertTrue(contentionList.size() > 0);
      List<AssessmentInfo> assessmentList = contentionList.get(0).getAssessments();
      assertTrue(assessmentList.size() > 0);
      foundAssessment = assessmentList.get(0);
      break;
    }
    assertNotNull(foundAssessment);
    assertEquals(expectedSufficientValue, foundAssessment.getSufficientEvidenceFlag());
  }

  /**
   * Runs a full end-to-end test for the collection id using mock services. Collection id used here
   * should be one of the preloaded ones in mock-mas-api amd the benefit claim id should one of the
   * ones in mock-bip-claims-api. This verifies rest message, pdf upload, rdr1 special issue removal
   * and lifecycle status update.
   */
  @SneakyThrows
  private void testAutomatedClaimFullPositive(String collectionId) {
    MasAutomatedClaimRequest request = startAutomatedClaim(collectionId);
    final String claimId = request.getClaimDetail().getBenefitClaimId();
    testPdfUpload(request);
    testUpdatedContentions(claimId, false, true, ClaimStatus.RFD);
    testLifecycleStatus(claimId, ClaimStatus.RFD);
  }

  /*
   * This uses the single available collection from MAS development server. Since the collection has
   * no blood pressure it is matched with a LH Health sandbox example without any recent blood
   * pressure to result in a off ramping example.
   */
  // Commenting out this for now. Looks like 350 is not working anymore. Throws exception.
  // @Test
  // void testAutomatedClaimMasExample() {
  //   AutomatedClaimTestSpec spec = specFor200("350");
  //   spec.setCheckSlack(true);
  //   testAutomatedClaimOffRamp(spec);
  // }

  /** Tests if Bip Claim Api 404 for non-existent claim results in 400 on our end. */
  @Test
  @SneakyThrows
  void testAutomatedClaimNonExistentClaimId() {
    var path = "test-mas/claim-801-7101-nonexistent-claimid.json";
    var content = resourceToString(path);
    var requestEntity = getBearerAuthEntity(content);
    try {
      restTemplate.postForEntity(AUTOMATED_CLAIM_URL, requestEntity, MasResponse.class);
      fail("Collection 801 should have received 400.");
    } catch (HttpStatusCodeException exception) {
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
  }

  @SneakyThrows
  private boolean testSlackMessage(String collectionId) {
    for (int pollNumber = 0; pollNumber < 60; ++pollNumber) {
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

  private void testSpecialIssuesRemoved(
      String claimId, ClaimContention contention, boolean rrdShouldBeRemoved) {
    List<String> specialIssueCodes = contention.getSpecialIssueCodes();
    assertNotNull(specialIssueCodes);

    String[] remainingRrdOrRdr1 =
        specialIssueCodes.stream()
            .filter(code -> code.equals("RDR1") || code.equals("RRD"))
            .toArray(String[]::new);

    if (rrdShouldBeRemoved) {
      String[] emptyArray = {};
      assertArrayEquals(emptyArray, remainingRrdOrRdr1, "RRD or RDR1 should have been removed");
    } else {
      String[] onlyRrd = {"RRD"};
      assertArrayEquals(onlyRrd, remainingRrdOrRdr1, "only RRD should have remained");
    }
  }

  private void testUpdatedContentions(
      String claimId,
      boolean rrdShouldBeRemoved,
      boolean automationIndicator,
      ClaimStatus claimStatus) {
    List<ClaimContention> contentions = getUpdatedContentions(claimId);
    assertNotNull(contentions, "Contentions are not updated to remove special issue(s).");
    log.info("Claim {} contentions have been updated.", claimId);

    // Only have single issue claims for now
    assertEquals(1, contentions.size());
    ClaimContention contention = contentions.get(0);

    testSpecialIssuesRemoved(claimId, contention, rrdShouldBeRemoved);
    assertEquals(automationIndicator, contention.isAutomationIndicator());
  }

  private void testLifecycleStatus(String claimId, ClaimStatus expectedStatus) {
    String status = getUpdatedLifecycleStatus(claimId);
    assertNotNull(status, "Lifecycle status has not been updated.");
    log.info("Claim {} lifecycle status is: {}", claimId, status);
    assertEquals(expectedStatus.getDescription(), status);
  }

  private void testAutomatedClaimOffRamp(AutomatedClaimTestSpec spec) {
    MasAutomatedClaimRequest request = startAutomatedClaim(spec);

    boolean slackResult = testSlackMessage(spec.getCollectionId());
    assertTrue(slackResult, "No or unexpected slack messages received by slack server");

    final String claimId = request.getClaimDetail().getBenefitClaimId();
    testUpdatedContentions(claimId, true, false, ClaimStatus.OPEN);
    testLifecycleStatus(claimId, ClaimStatus.OPEN);
  }

  /**
   * Out of scope test case because of disability action type. Rest response message, Slack message
   * and removal of RDR1 special issue are verified.
   */
  @Test
  void testAutomatedClaimOutOfScope() {
    AutomatedClaimTestSpec spec = new AutomatedClaimTestSpec("10");
    spec.setPayloadPath("test-mas/claim-10-7101-outofscope.json");
    spec.setExpectedMessage(
        "Claim with [collection id = 10], [diagnostic code = 7101], and "
            + "[disability action type = DECREASE] is not in scope.");

    testAutomatedClaimOffRamp(spec);
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
        "Claim with [collection id = 20] does not qualify for "
            + "automated processing because it is missing anchors.");

    testAutomatedClaimOffRamp(spec);
  }

  private ExamOrderInfoResponse findExamOrderInfoForCollectionId(
      ExamOrderInfoResponse[] infoArray, String collectionId) {
    for (ExamOrderInfoResponse info : infoArray) {
      if (collectionId.equals(info.getCollectionId())) {
        return info;
      }
    }
    return null;
  }

  private void checkExamOrderInfo(String collectionId, String status, boolean isOrderedAtNull) {
    log.info("Getting exam order info from ");
    HttpEntity<Void> requestEntity = getTokenAuthHeaders();

    ResponseEntity<ExamOrderInfoResponse[]> response =
        restTemplate.exchange(
            EXAM_ORDER_INFO_URL, HttpMethod.GET, requestEntity, ExamOrderInfoResponse[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ExamOrderInfoResponse[] infoArray = response.getBody();
    ExamOrderInfoResponse info = findExamOrderInfoForCollectionId(infoArray, collectionId);
    assertNotNull(info, "Cannot find database entry for collection " + collectionId);
    assertEquals(status, info.getStatus());
    assertEquals(isOrderedAtNull, info.getOrderedAt() == null);
  }

  private void testAutomatedClaimOrderExam(AutomatedClaimTestSpec spec) {
    String collectionId = spec.getCollectionId();
    log.info("testing ordering exam for collection {}", collectionId);
    MasAutomatedClaimRequest request = startAutomatedClaim(spec);
    if (spec.isMasError()) {
      boolean slackResult = testSlackMessage(spec.getCollectionId());
      assertTrue(slackResult, "No or unexpected slack messages received by slack server");
      String claimId = request.getClaimDetail().getBenefitClaimId();
      testUpdatedContentions(claimId, true, true, ClaimStatus.OPEN);
    } else {
      testExamOrdered(collectionId);
      if (spec.isBipError()) {
        boolean slackResult = testSlackMessage(spec.getCollectionId());
        assertTrue(slackResult, "No or unexpected slack messages received by slack server");
      } else {
        testPdfUpload(request);
      }
      String claimId = request.getClaimDetail().getBenefitClaimId();
      testUpdatedContentions(claimId, false, true, ClaimStatus.OPEN);
      testLifecycleStatus(claimId, ClaimStatus.OPEN);
      checkExamOrderInfo(collectionId, "ORDER_SUBMITTED", true);
      testExamOrderingStatus(collectionId);
      checkExamOrderInfo(collectionId, MAS_ORDER_NOTIFY_STATUS, false);
    }
  }

  /**
   * Test Case that ensures that exam order *is* called. The data underlying follows the NEW claim,
   * one relevant condition, not enough information path. Rest response message, exam being ordered,
   * pdf upload, and removal of RDR1 special issue are verified.
   */
  @Test
  void testAutomatedClaimOrderExamNewClaim() {
    AutomatedClaimTestSpec spec = specFor200("377");
    testAutomatedClaimOrderExam(spec);
  }

  /**
   * Test Case that ensures that exam order *is* called. The data underlying follows the "increase"
   * claim path where not enough blood pressure readings exist. Rest response message, exam being
   * ordered, pdf upload, and removal of RDR1 special issue are verified.
   */
  @Test
  void testAutomatedClaimOrderExamIncreaseClaim() {
    AutomatedClaimTestSpec spec = specFor200("378");
    testAutomatedClaimOrderExam(spec);
  }

  /**
   * Test Case that ensures bip claim evidence api upload evidence errors are handled properly.
   * Copied from 378; this sends an error message instead of uploading the pdf.
   */
  @Test
  void testAutomatedClaimOrderExamBipError() {
    AutomatedClaimTestSpec spec = specFor200("390");
    spec.setBipError(true);
    testAutomatedClaimOrderExam(spec);
  }

  /**
   * Test Case that ensures mas order exam errors are handled properly. Copied from 378; this sends
   * an error message instead of ordering the exam or uploading the pdf.
   */
  @Test
  void testAutomatedClaimOrderExamMasError() {
    AutomatedClaimTestSpec spec = specFor200("391");
    spec.setMasError(true);
    testAutomatedClaimOrderExam(spec);
  }

  /**
   * Test for an automated claim that does NOT order exam, and then fails PDF upload (Bip Errors on
   * this fileID) Ensures that we handle the pdf failure correctly.
   */
  @Test
  void testAutomatedClaimNoExamPDFError() {
    String collectionId = "392";
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    testAutomatedClaimOffRamp(spec);
  }

  /** Test Case that ensures that exam order *is* called based on LH data with no MAS annotations */
  @Test
  void testLHDataOnlyClaimOrderExamIncreaseClaim() {
    AutomatedClaimTestSpec spec = specFor200("401");
    testAutomatedClaimOrderExam(spec);
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
  private String getOrderingStatusValidRequest(String collectionId) {
    var payload =
        Map.of(
            "collectionId",
            collectionId,
            "collectionStatus",
            MAS_ORDER_NOTIFY_STATUS,
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
   * This is a full positive end-to-end test for an increase case. See
   * testAutomatedClaimFullPositiveTwo to see what is being verified. After the run get the pdf from
   * http://localhost:8096/received-files/9999375
   */
  @Test
  void testAutomatedClaimFullPositiveIncrease() {
    testAutomatedClaimFullPositive("375");
  }

  /** This is a full positive end-to-end test for an increase case with LightHouse data only. */
  @Test
  void testLHDataOnlyClaimFullPositiveIncrease() {
    testAutomatedClaimFullPositive("400");
  }

  /**
   * This is a full positive end-to-end test for an presumptive case. See
   * testAutomatedClaimFullPositiveTwo to see what is being verified. After the run get the pdf from
   * http://localhost:8096/received-files/9999376
   */
  @Test
  void testAutomatedClaimFullPositivePresumptive() {
    testAutomatedClaimFullPositive("376");
  }

  /**
   * This is a full positive end-to-end test for a case with incomplete blood pressures. See
   * testAutomatedClaimFullPositiveTwo to see what is being verified. After the run get the pdf from
   * http://localhost:8096/received-files/9999380
   */
  @Test
  void testAutomatedClaimFullPositiveIncompleteBloodPressures() {
    testAutomatedClaimFullPositive("380");
  }

  /**
   * This is an off-ramp test case with missing blood pressure data for presumptive. Rest message,
   * Slack message, removal of rdr1, and database update are verified.
   */
  @Test
  void testAutomatedClaimSufficiencyIsNull() {
    String collectionId = "500";
    AutomatedClaimTestSpec spec = specFor200(collectionId);
    testAutomatedClaimOffRamp(spec);
    testClaimSufficientStatus(collectionId, null);
  }
}
