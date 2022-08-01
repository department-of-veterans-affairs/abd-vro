package gov.va.vro.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

/**
 * Used to programmatically inject messages into a Camel endpoint. AKA an entrance ramp onto a Camel
 * route. Intended to be used by Controller classes to initiate routing requests.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CamelEntrance {

  private final ProducerTemplate producerTemplate;

  @Deprecated // part of the demo code
  public ClaimSubmission postClaim(ClaimSubmission claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody("direct:postClaim", claim, ClaimSubmission.class);
  }

  @Deprecated // part of the demo code
  public String assess_health_data_demo(AssessHealthData resource) {
    return producerTemplate.requestBody("direct:assess_health_data_demo", resource, String.class);
  }

  private final ObjectMapper mapper = new ObjectMapper();

  public String submitClaim(Claim claim) {
    return producerTemplate.requestBody("direct:claim-submit", claim, String.class);
  }

  public String submitClaimFull(Claim claim) {
    return producerTemplate.requestBody("direct:claim-submit-full", claim, String.class);
  }

  public String generatePdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody("direct:generate-pdf", resource, String.class);
  }

  public String fetchPdf(String claimSubmissionId) {
    return producerTemplate.requestBody("direct:fetch-pdf", claimSubmissionId, String.class);
  }
}
