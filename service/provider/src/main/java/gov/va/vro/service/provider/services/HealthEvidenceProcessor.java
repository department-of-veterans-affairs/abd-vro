package gov.va.vro.service.provider.services;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Collections;
import java.util.List;

@Slf4j
public class HealthEvidenceProcessor implements Processor {

  /** Set the flag "sufficient for fast-tracking". */
  @Override
  public void process(Exchange exchange) {
    MasProcessingObject masTransferObject = (MasProcessingObject) exchange.getProperty("payload");

    AbdEvidenceWithSummary evidence = exchange.getMessage().getBody(AbdEvidenceWithSummary.class);

    if (evidence.getErrorMessage() != null) {
      log.error("Health Assessment Failed");
      if ("insufficientHealthDataToOrderExam".equalsIgnoreCase(evidence.getErrorMessage())) {
        exchange.setProperty("sufficientForFastTracking", null);
      }
      throw new MasException("Health Assessment Failed with error:" + evidence.getErrorMessage());
    } else {
      exchange.setProperty("sufficientForFastTracking", evidence.isSufficientForFastTracking());
      log.info(
          " MAS Processing >> Sufficient Evidence >>> " + evidence.isSufficientForFastTracking());
      masTransferObject.setEvidence(getValidEvidence(evidence.getEvidence()));
      exchange.getMessage().setBody(masTransferObject);
    }
  }

  /**
   * The list must be provided to the PDF processor, even if they are empty. Otherwise, it will fail
   * to process
   */
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
