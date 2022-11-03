package gov.va.vro.service.provider.services;

import gov.va.vro.model.AbdEvidenceSummaryDocument;
import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvidenceSummaryDocumentProcessor implements Processor {

  private final SaveToDbService saveToDbService;

  @Override
  public void process(Exchange exchange) {
    AbdEvidenceSummaryDocument response =
        exchange.getIn().getBody(AbdEvidenceSummaryDocument.class);
    if (response == null) {
      log.warn("Response from camel was null, returning.");
      return;
    }
    if (StringUtils.equals(response.getStatus(), "NOT_FOUND")) {
      log.warn("PDF not found, exiting.");
      return;
    }
    String documentName = getDocumentName(response);
    if (documentName == null) {
      log.warn("Could not get document name, exiting.");
      return;
    }
    saveToDbService.insertEvidenceSummaryDocument(
        response.getClaimSubmissionId(), documentName, response.getStatus());
  }

  public String getDocumentName(AbdEvidenceSummaryDocument evidenceSummaryDocument) {
    String timestamp = String.format("%1$tY%1$tm%1$td", new Date());
    String diagnosis = StringUtils.capitalize(evidenceSummaryDocument.getDiagnosis());
    return String.format("VAMC_%s_Rapid_Decision_Evidence--%s.pdf", diagnosis, timestamp);
  }
}
