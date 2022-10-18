package gov.va.vro.end2end.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TestSetup {
  final static private Map<String, String> diagnosticCodeToName =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<>("7101", "Hypertension"),
          new AbstractMap.SimpleEntry<>("6602", "Asthma")
      );
  final static private AtomicInteger claimSubmissionCounter = new AtomicInteger(7000);

  private String assessment;
  private String veteranInfo;

  private String claimSubmissionId;

  private JsonNode assessmentNode;
  private JsonNode veteranInfoNode;

  private ObjectMapper mapper = new ObjectMapper();

  private String getResource(String path) throws Exception {
    InputStream stream = this.getClass().getResourceAsStream(path);
    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
  }

  public String getDiagnosticCode() {
    return assessmentNode.get("diagnosticCode").asText();
  }

  public String getAssessmentRequest() {
    String veteranIcn = assessmentNode.get("veteranIcn").asText();

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", claimSubmissionId);
    result.put("veteranIcn", veteranIcn);
    result.put("diagnosticCode", getDiagnosticCode());

    return result.toString();
  }

  public String getGeneratePdfRequest() {
    JsonNode evidence = assessmentNode.get("evidence");

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", claimSubmissionId);
    result.put("diagnosticCode", getDiagnosticCode());
    result.set("veteranInfo", veteranInfoNode);
    result.set("evidence",  evidence);

    return result.toString();
  }

  public String getGeneratePdfResponse() {
    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", claimSubmissionId);
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

  public String getContentDisposition() {
    Instant instant = Instant.now();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("UTC"));
    String date = dtf.format(instant);
    String diagnosticCode = getDiagnosticCode();
    String dcName = diagnosticCodeToName.get(diagnosticCode);

    String filename = "VAMC_" + dcName + "_Rapid_Decision_Evidence--" + date + ".pdf";
    return "attachment; filename=\"" + filename + "\"";
  }

  public static TestSetup getInstance(String resourceDir) throws Exception {
    TestSetup result = new TestSetup();

    String assessmentPath = String.format("/%s/assessment.json", resourceDir);
    result.assessment = result.getResource(assessmentPath);
    result.assessmentNode = result.mapper.readTree(result.assessment);

    String veteranInfoPath = String.format("/%s/veteranInfo.json", resourceDir);
    result.veteranInfo = result.getResource(veteranInfoPath);
    result.veteranInfoNode = result.mapper.readTree(result.veteranInfo);

    int counterValue = claimSubmissionCounter.incrementAndGet();
    result.claimSubmissionId = String.valueOf(counterValue);

    return result;
  }
}
