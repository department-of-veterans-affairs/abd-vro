package gov.va.vro.service.provider;

import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CamelEntrance {
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
