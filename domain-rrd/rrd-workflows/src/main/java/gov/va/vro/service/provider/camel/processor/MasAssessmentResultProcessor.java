package gov.va.vro.service.provider.camel.processor;

import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasAssessmentResultProcessor implements Processor {

  private final SaveToDbService saveToDbService;

  @Override
  public void process(Exchange exchange) throws Exception {
    var evidence = exchange.getMessage().getBody(AbdEvidenceWithSummary.class);
    String diagnosticCode = exchange.getProperty("diagnosticCode", String.class);
    String idType = exchange.getProperty("idType", String.class);
    evidence.setIdType(idType);
    if (diagnosticCode == null) {
      log.warn("Diagnostic Code was empty, exiting.");
      return;
    }
    saveToDbService.insertAssessmentResult(evidence, diagnosticCode);
  }
}
