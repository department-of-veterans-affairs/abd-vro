package gov.va.vro.service.provider.services;

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
  public void process(Exchange exchange) throws Exception {
    UUID claimId = (UUID) exchange.getProperty("claim-id");
    String diagnosticCode = (String) exchange.getProperty("diagnosticCode");
    String evidence = (String) exchange.getIn().getBody();
    saveToDbService.insertAssessmentResult(claimId, evidence, diagnosticCode);
  }
}
