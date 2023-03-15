package gov.va.vro.service.provider.services;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.ServiceLocation;
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
    List<ServiceLocation> serviceLocations =
        (List<ServiceLocation>) exchange.getProperty("serviceLocations");
    List<String> docsWoutAnnotsChecked =
        (List<String>) exchange.getProperty("docsWoutAnnotsChecked");

    AbdEvidenceWithSummary evidence = exchange.getMessage().getBody(AbdEvidenceWithSummary.class);

    String evidenceError = evidence.getErrorMessage();
    if (evidenceError != null) {
      log.error("Health Assessment Failed");
      throw new MasException("Health Assessment Failed with error:" + evidence.getErrorMessage());
    }

    if ("insufficientHealthDataToOrderExam".equalsIgnoreCase(evidenceError)) {
      exchange.setProperty("sufficientForFastTracking", null);
    }

    exchange.setProperty("sufficientForFastTracking", evidence.getSufficientForFastTracking());
    log.info(
        " MAS Processing >> Sufficient Evidence >>> " + evidence.getSufficientForFastTracking());

    // Transfer service locations. Assessment does not populate that one.
    AbdEvidence currentEvidenceData = evidence.getEvidence();
    currentEvidenceData.setServiceLocations(serviceLocations);
    currentEvidenceData.setDocumentsWithoutAnnotationsChecked(docsWoutAnnotsChecked);

    masTransferObject.setEvidence(getValidEvidence(currentEvidenceData));
    exchange.getMessage().setBody(masTransferObject);
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
    validEvidence.setServiceLocations(emptyIfNull(evidence.getServiceLocations()));
    List<String> docsWoutAnnotsChecked = evidence.getDocumentsWithoutAnnotationsChecked();
    validEvidence.setDocumentsWithoutAnnotationsChecked(emptyIfNull(docsWoutAnnotsChecked));

    return validEvidence;
  }

  private <T> List<T> emptyIfNull(List<T> list) {
    return list == null ? Collections.emptyList() : list;
  }
}
