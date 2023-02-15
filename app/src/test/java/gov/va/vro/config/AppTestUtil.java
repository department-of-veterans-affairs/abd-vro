package gov.va.vro.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.service.spi.model.Claim;
import lombok.SneakyThrows;

/** Common utility methods used by the app tests. */
public class AppTestUtil {
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * JSON object to string.
   *
   * @param o object to stringify.
   * @return json as string.
   */
  @SneakyThrows
  public String toJsonString(Object o) {
    return mapper.writeValueAsString(o);
  }

  /**
   * Claim object to string response.
   *
   * @param claim claim.
   * @param evidence evidence.
   * @return claim response to string.
   */
  @SneakyThrows
  public String claimToResponse(Claim claim, boolean evidence, String errorMessage) {
    var response = new HealthDataAssessment();
    response.setDiagnosticCode(claim.getDiagnosticCode());
    response.setVeteranIcn(claim.getVeteranIcn());
    // Collection id is equivalent to the reference_id on the claim_submission table, which is what other entities
    // Expect to see as the claim submission id.
    response.setClaimSubmissionId(claim.getCollectionId());
    response.setErrorMessage(errorMessage);
    if (evidence) {
      response.setEvidence(new AbdEvidence());
    }
    return mapper.writeValueAsString(response);
  }
}
