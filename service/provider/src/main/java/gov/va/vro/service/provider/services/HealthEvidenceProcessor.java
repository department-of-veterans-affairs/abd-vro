package gov.va.vro.service.provider.services;

import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasTransferObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Slf4j
public class HealthEvidenceProcessor implements Processor {
  @Override
  public void process(Exchange exchange) {
    MasAutomatedClaimPayload claimPayload =
        (MasAutomatedClaimPayload) exchange.getProperty("claim");

    AbdEvidenceWithSummary evidence = exchange.getMessage().getBody(AbdEvidenceWithSummary.class);

    if (evidence.getErrorMessage() != null) {
      log.error("Health Assessment Failed");
      throw new MasException("Health Assessment Failed with error:" + evidence.getErrorMessage());
    } else {
      exchange.setProperty("sufficientForFastTracking", evidence.getSufficientForFastTracking());
      log.info(
          " MAS Processing >> Sufficient Evidence >>> " + evidence.getSufficientForFastTracking());
      var masTransferObject = new MasTransferObject(claimPayload, evidence.getEvidence());
      exchange.getMessage().setBody(masTransferObject);
    }
  }
}
