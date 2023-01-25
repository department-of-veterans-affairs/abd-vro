package gov.va.vro.service.provider.services;

import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvidenceSummaryDocumentProcessor implements Processor {
  private final SaveToDbService saveToDbService;

  @Override
  public void process(Exchange exchange) {
    GeneratePdfPayload payload = exchange.getIn().getBody(GeneratePdfPayload.class);
    if (payload == null) {
      log.warn("Payload is empty, returning...");
      return;
    }
    String diagnosis = matchDiagnosticCode(payload.getDiagnosticCode());
    if (diagnosis == null) {
      log.warn("Could not match diagnostic code with a diagnosis, exiting.");
      return;
    }
    saveToDbService.insertEvidenceSummaryDocument(
        payload, GeneratePdfPayload.createPdfFilename(diagnosis));
  }

  private String matchDiagnosticCode(String diagnosticCode) {
    String diagnosis = DiagnosisLookup.getDiagnosis(diagnosticCode);
    if (diagnosis == null) {
      log.warn("Could not match diagnostic code with a diagnosis, exiting.");
      return null;
    }
    return diagnosis;
  }
}
