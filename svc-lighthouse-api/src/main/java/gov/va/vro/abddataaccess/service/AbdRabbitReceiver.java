package gov.va.vro.abddataaccess.service;

import gov.va.vro.abddataaccess.model.AbdClaim;
import gov.va.vro.abddataaccess.model.AbdResponse;
import gov.va.vro.model.AbdEvidence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AbdRabbitReceiver {
  @Autowired private FhirClient client;

  @RabbitListener(queues = "${abd-data-access.rabbitmq.claim-submit-queue}")
  AbdResponse receiveMessage(AbdClaim claim) {
    log.info(
        "Claim submission for icn={}, claim={}",
        claim.getVeteranIcn(),
        claim.getClaimSubmissionId());
    try {
      AbdEvidence evidence = client.getMedicalEvidence(claim);
      AbdResponse response = new AbdResponse(claim, evidence);
      return response;
    } catch (Exception e) {
      // Message can be null despite exception existing.
      log.error(e.getMessage());
      AbdResponse response = new AbdResponse(claim);
      response.setErrorMessage("Lighthouse Error: " + e.getMessage());
      return response;
    }
  }
}
