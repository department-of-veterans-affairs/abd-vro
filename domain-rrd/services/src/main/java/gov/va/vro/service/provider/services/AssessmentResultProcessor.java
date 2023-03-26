package gov.va.vro.service.provider.services;

import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentResultProcessor implements Processor {

  private final SaveToDbService saveToDbService;

  @Override
  public void process(Exchange exchange) {
    UUID claimId = exchange.getProperty("claim-id", UUID.class);
    if (claimId == null) {
      log.warn("Claim Id was empty, exiting");
      return;
    }
    String diagnosticCode = exchange.getProperty("diagnosticCode", String.class);
    if (diagnosticCode == null) {
      log.warn("Diagnostic Code was empty, exiting.");
      return;
    }
    AbdEvidenceWithSummary evidence = exchange.getIn().getBody(AbdEvidenceWithSummary.class);
    if (evidence == null) {
      log.warn("Evidence was empty, exiting");
      return;
    }
    saveToDbService.insertAssessmentResult(claimId, evidence, diagnosticCode);
  }
}
