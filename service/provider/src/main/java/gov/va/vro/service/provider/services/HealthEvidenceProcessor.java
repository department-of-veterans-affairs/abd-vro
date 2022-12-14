package gov.va.vro.service.provider.services;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasTransferObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Collections;
import java.util.List;

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
      exchange.setProperty("sufficientForFastTracking", evidence.isSufficientForFastTracking());
      log.info(
          " MAS Processing >> Sufficient Evidence >>> " + evidence.isSufficientForFastTracking());
      var masTransferObject =
          new MasTransferObject(claimPayload, getValidEvidence(evidence.getEvidence()));
      exchange.getMessage().setBody(masTransferObject);
    }
  }

  private AbdEvidence getValidEvidence(AbdEvidence evidence) {
    var validEvidence = new AbdEvidence();
    validEvidence.setConditions(emptyIfNull(evidence.getConditions()));
    validEvidence.setMedications(emptyIfNull(evidence.getMedications()));
    validEvidence.setBloodPressures(emptyIfNull(evidence.getBloodPressures()));
    validEvidence.setProcedures(emptyIfNull(evidence.getProcedures()));
    return validEvidence;
  }

  private <T> List<T> emptyIfNull(List<T> list) {
    return list == null ? Collections.emptyList() : list;
  }
}
