package gov.va.vro.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
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
  // Provided by Camel https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html
  private final ProducerTemplate producerTemplate;

  private final ObjectMapper mapper = new ObjectMapper();

  public ClaimSubmission postClaim(ClaimSubmission claim) {
    // https://camel.apache.org/manual/producertemplate.html#_send_vs_request_methods
    return producerTemplate.requestBody(PrimaryRoutes.CHANNEL_DIRECT_POST_CLAIM, claim, ClaimSubmission.class);
  }


  @Deprecated // demo route
  public String assess_health_data_demo(AssessHealthData resource) {
    return producerTemplate.requestBody("direct:assess_health_data_demo", resource, String.class);
  }

  @Deprecated // demo route
  public String generate_pdf_demo(GeneratePdfPayload resource) {
    return producerTemplate.requestBody("direct:generate_pdf_demo", resource, String.class);
  }
}
