package gov.va.vro.end2end.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.util.Json;
import lombok.Getter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
public class TestSetup {
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

  public String getAssessmentInput() {
    String veteranIcn = assessmentNode.get("veteranIcn").asText();
    String diagnosticCode = assessmentNode.get("diagnosticCode").asText();

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", claimSubmissionId);
    result.put("veteranIcn", veteranIcn);
    result.put("diagnosticCode", diagnosticCode);

    return result.toString();
  }

  public String getGeneratePdfInput() {
    String diagnosticCode = assessmentNode.get("diagnosticCode").asText();
    JsonNode evidence = assessmentNode.get("evidence");

    ObjectNode result = mapper.createObjectNode();
    result.put("claimSubmissionId", claimSubmissionId);
    result.put("diagnosticCode", diagnosticCode);
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

  public static TestSetup getInstance(String resourceDir, String claimSubmissionId) throws Exception {
    TestSetup result = new TestSetup();

    String assessmentPath = String.format("/%s/assessment.json", resourceDir);
    result.assessment = result.getResource(assessmentPath);
    result.assessmentNode = result.mapper.readTree(result.assessment);

    String veteranInfoPath = String.format("/%s/veteranInfo.json", resourceDir);
    result.veteranInfo = result.getResource(veteranInfoPath);
    result.veteranInfoNode = result.mapper.readTree(result.veteranInfo);

    result.claimSubmissionId = claimSubmissionId;

    return result;
  }
}
