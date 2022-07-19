package gov.va.vro.service.provider;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.spi.db.model.Claim;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
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

  public String processClaim(Claim claim) {
    return producerTemplate.requestBody(PrimaryRoutes.ROUTE_PROCESS_CLAIM, claim, String.class);
  }

  @Deprecated // part of the demo code
  public ClaimSubmission postClaim(ClaimSubmission claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody("direct:postClaim", claim, ClaimSubmission.class);
  }

  @Deprecated // part of the demo code
  public String assess_health_data_demo(AssessHealthData resource) {
    return producerTemplate.requestBody("direct:assess_health_data_demo", resource, String.class);
  }

  @Deprecated // part of the demo code
  public String generate_pdf_demo(GeneratePdfPayload resource) {
    return producerTemplate.requestBody("direct:generate_pdf_demo", resource, String.class);
  }
}
