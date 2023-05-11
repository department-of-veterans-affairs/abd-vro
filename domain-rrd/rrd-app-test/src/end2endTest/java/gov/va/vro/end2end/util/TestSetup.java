package gov.va.vro.end2end.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.va.vro.service.provider.utils.DiagnosisLookup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * This class drives end-to-end tests based on two gold files in a resource directory. The file
 * assessment.json is the expected response from the server for assessment. The file
 * veteranInfo.json provides the veteran information needed for pdf generation.
 */
@Getter
@RequiredArgsConstructor
public class TestSetup {

  private final String name;

  private String assessment;
  private String veteranInfo;

  private JsonNode assessmentNode;
  private JsonNode veteranInfoNode;

  private final ObjectMapper mapper = new ObjectMapper();

  private String getResource(String path) throws Exception {
    InputStream stream = this.getClass().getResourceAsStream(path);
    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
  }

  public String getDiagnosticCode() {
    return assessmentNode.get("diagnosticCode").asText();
  }

  public String getClaimSubmissionId() {
    return assessmentNode.get("claimSubmissionId").asText();
  }

  /**
   * Provides assessment request for server.
   *
   * @return json assessment request
   */
  public String getAssessmentRequest() {
    String veteranIcn = assessmentNode.get("veteranIcn").asText();

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", getClaimSubmissionId());
    result.put("veteranIcn", veteranIcn);
    result.put("diagnosticCode", getDiagnosticCode());

    return result.toString();
  }

  /**
   * Provides generate pdf request for server.
   *
   * @return json generate pdf request
   */
  public String getGeneratePdfRequest() {
    JsonNode evidence = assessmentNode.get("evidence");

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", getClaimSubmissionId());
    result.put("diagnosticCode", getDiagnosticCode());
    result.set("veteranInfo", veteranInfoNode);
    result.set("evidence", evidence);

    return result.toString();
  }

  /**
   * Returns expected json generate pdf response.
   *
   * @return expected json request
   */
  public String getGeneratePdfResponse() {
    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", getClaimSubmissionId());
    result.put("status", "COMPLETE");

    return result.toString();
  }

  public JsonNode getBpReadingsNode() {
    JsonNode evidence = assessmentNode.get("evidence");
    return evidence.get("bp_readings");
  }

  public JsonNode getMedicationsNode() {
    JsonNode evidence = assessmentNode.get("evidence");
    return evidence.get("medications");
  }

  /**
   * Returns expected content disposition filename.
   *
   * @return expected content disposition filename
   */
  public String getContentDispositionFilename() {
    Instant instant = Instant.now();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("UTC"));
    String date = dtf.format(instant);
    String diagnosticCode = getDiagnosticCode();
    String dcName = DiagnosisLookup.getDiagnosis(diagnosticCode);
    return "VAMC_" + dcName + "_Rapid_Decision_Evidence--" + date + ".pdf";
  }

  /**
   * Constructs a new test set up based on gold files in the resource directory.
   *
   * @param resourceDir resource directory
   * @return newed TestSetup
   * @throws Exception any error to fail the test
   */
  public static TestSetup getInstance(String resourceDir) throws Exception {
    TestSetup result = new TestSetup(resourceDir);

    String assessmentPath = String.format("/%s/assessment.json", resourceDir);
    result.assessment = result.getResource(assessmentPath);
    result.assessmentNode = result.mapper.readTree(result.assessment);

    String veteranInfoPath = String.format("/%s/veteranInfo.json", resourceDir);
    result.veteranInfo = result.getResource(veteranInfoPath);
    result.veteranInfoNode = result.mapper.readTree(result.veteranInfo);

    return result;
  }
}
