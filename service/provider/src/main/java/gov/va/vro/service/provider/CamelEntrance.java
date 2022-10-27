package gov.va.vro.service.provider;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
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

  public String submitClaim(Claim claim) {
    return producerTemplate.requestBody(PrimaryRoutes.ENDPOINT_SUBMIT_CLAIM, claim, String.class);
  }

  public String submitClaimFull(Claim claim) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_SUBMIT_CLAIM_FULL, claim, String.class);
  }

  public String generatePdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_GENERATE_PDF, resource, String.class);
  }

  public String fetchPdf(String claimSubmissionId) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_FETCH_PDF, claimSubmissionId, String.class);
  }

  public void notifyAutomatedClaim(MasAutomatedClaimPayload payload, long delay) {
    producerTemplate.sendBodyAndHeader(
        PrimaryRoutes.ENDPOINT_AUTOMATED_CLAIM, payload, PrimaryRoutes.MAS_DELAY_PARAM, delay);
  }

  public String examOrderingStatus(MasExamOrderStatusPayload payload) {
    return "Message Received";
  }
}
