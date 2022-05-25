package gov.va.vro.routes;

import gov.va.vro.model.Claim;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class CamelEntrance {
  @Autowired private ProducerTemplate producerTemplate;

  public Claim postClaim(Claim claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody("direct:postClaim", claim, Claim.class);
  }

  public Claim waitForStatusChange(Claim claim) {
    return null;
  }
}
