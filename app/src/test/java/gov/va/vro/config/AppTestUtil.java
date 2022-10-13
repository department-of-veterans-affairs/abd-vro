package gov.va.vro.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.service.spi.model.Claim;
import lombok.SneakyThrows;

public class AppTestUtil {
  private final ObjectMapper mapper = new ObjectMapper();

  @SneakyThrows
  public String toJsonString(Object o) {
    return mapper.writeValueAsString(o);
  }

  @SneakyThrows
  public String claimToResponse(Claim claim, boolean evidence) {
    var response = new HealthDataAssessmentResponse();
    response.setDiagnosticCode(claim.getDiagnosticCode());
    response.setVeteranIcn(claim.getVeteranIcn());
    response.setErrorMessage("I am not a real endpoint.");
    if (evidence) {
      response.setEvidence(new AbdEvidence());
    }
    return mapper.writeValueAsString(response);
  }
}
