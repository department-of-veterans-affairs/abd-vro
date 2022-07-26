package gov.va.vro.abd_data_access.service;

import gov.va.vro.abd_data_access.model.AbdClaim;
import gov.va.vro.abd_data_access.model.AbdEvidence;
import gov.va.vro.abd_data_access.model.AbdResponse;
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
    log.info("Claim submission for icn={}", claim.getVeteranIcn());
    try {
      AbdEvidence evidence = client.getMedicalEvidence(claim);
      AbdResponse response = new AbdResponse(claim, evidence);
      return response;
    } catch (Exception e) {
      log.error(e.getMessage());
      AbdResponse response = new AbdResponse(claim);
      response.setErrorMessage(e.getMessage());
      return response;
    }
  }
}
