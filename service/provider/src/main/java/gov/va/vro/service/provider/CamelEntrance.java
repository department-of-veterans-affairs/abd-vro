package gov.va.vro.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.spi.db.model.Claim;
import gov.va.vro.service.spi.demo.model.ClaimPayload;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
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

  public Claim processClaim(Claim claim) {
    return producerTemplate.requestBody(PrimaryRoutes.ENDPOINT_PROCESS_CLAIM, claim, Claim.class);
  }

  @Deprecated // part of the demo code
  public ClaimSubmission postClaim(ClaimSubmission claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody("direct:postClaim", claim, ClaimSubmission.class);
  }

  private final ObjectMapper mapper = new ObjectMapper();

  public String assessHealthData(ClaimPayload claim) {
    // String tmpRequest =
    // "{\"veteranIcn\":\"9000682\",\"diagnosticCode\":7101,\"claimSubmissionId\":\"1234\"}";
    return producerTemplate.requestBody("direct:assess_health_data", claim, String.class);
  }

  public String generate_pdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody("direct:generate_pdf", resource, String.class);
  }

  public String fetch_pdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody("direct:fetch_pdf", resource, String.class);
  }
}
