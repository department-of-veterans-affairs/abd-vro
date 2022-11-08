package gov.va.vro.service.provider.services;

import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvidenceSummaryDocumentProcessor implements Processor {

  private final SaveToDbService saveToDbService;

  @Override
  public void process(Exchange exchange) {
    GeneratePdfPayload response = exchange.getIn().getBody(GeneratePdfPayload.class);
    if (response == null) {
      log.warn("Response from camel was null, returning.");
      return;
    }
    String timestamp = String.format("%1$tY%1$tm%1$td", new Date());
    String diagnosis = StringUtils.capitalize(matchDiagnosticCode(response.getDiagnosticCode()));
    if (diagnosis == null) {
      log.warn("Could not match diagnostic code with a diagnosis, exiting.");
      return;
    }
    String documentName =
        String.format("VAMC_%s_Rapid_Decision_Evidence--%s.pdf", diagnosis, timestamp);

    saveToDbService.insertEvidenceSummaryDocument(response, documentName);
  }

  private String matchDiagnosticCode(String diagnosticCode) {
    Map<String, String> diagnosisMap = new HashMap<>();
    diagnosisMap.put("7101", "hypertension");
    diagnosisMap.put("6602", "asthma");
    String diagnosis = diagnosisMap.get(diagnosticCode);
    if (diagnosis == null) {
      log.warn("Could not match diagnostic code with a diagnosis, exiting.");
      return null;
    }
    return diagnosis;
  }
}
