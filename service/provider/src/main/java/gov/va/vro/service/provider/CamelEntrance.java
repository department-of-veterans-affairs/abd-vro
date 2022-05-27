package gov.va.vro.service.provider;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Used to programmatically inject messages into a Camel endpoint.
 * AKA an entrance ramp onto a Camel route.
 * Typically called by Controller classes.
 */
@Service
public class CamelEntrance {
  // Provided by Camel https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html
  @Autowired private ProducerTemplate producerTemplate;

  public ClaimSubmission postClaim(ClaimSubmission claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody("direct:postClaim", claim, ClaimSubmission.class);
  }

  public ClaimSubmission waitForStatusChange(ClaimSubmission claim) {
    // TODO if needed
    return null;
  }
}
