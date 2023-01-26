package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.service.provider.services.MasAssessmentResultProcessor;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.Claim;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class MasAssessmentResultProcessorTest extends BaseIntegrationTest {

  @Autowired MasAssessmentResultProcessor processor;

  @Autowired SaveToDbService saveToDbService;

  @Autowired AssessmentResultRepository assessmentResultRepository;

  @Test
  void testPersist() throws Exception {
    String claimSubmissionId = "666";
    String diagnosticCode = "999";
    String flag = "false";

    Claim claim = new Claim();

    claim.setClaimSubmissionId(claimSubmissionId);
    claim.setIdType(Claim.DEFAULT_ID_TYPE);
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode(diagnosticCode);
    saveToDbService.insertClaim(claim);

    var evidence = new AbdEvidenceWithSummary();
    evidence.setClaimSubmissionId(claimSubmissionId);
    evidence.setEvidenceSummary(Map.of("Hello", 10));

    var message = Mockito.mock(Message.class);
    var exchange = Mockito.mock(Exchange.class);
    Mockito.when(exchange.getMessage()).thenReturn(message);
    Mockito.when(exchange.getProperty("diagnosticCode", String.class)).thenReturn(diagnosticCode);
    Mockito.when(exchange.getProperty("sufficientForFastTracking", String.class)).thenReturn(flag);

    Mockito.when(message.getBody(AbdEvidenceWithSummary.class)).thenReturn(evidence);
    processor.process(exchange);

    claimRepository
        .findByClaimSubmissionIdAndIdType(claimSubmissionId, Claim.DEFAULT_ID_TYPE)
        .orElseThrow();
    var results =
        assessmentResultRepository.findAll().stream()
            .filter(result -> diagnosticCode.equals(result.getContention().getDiagnosticCode()))
            .toList();
    assertEquals(1, results.size());
    var result = results.get(0);
    var summary = result.getEvidenceCountSummary();
    assertEquals("10", summary.get("Hello"));
    assertEquals(false, result.getSufficientEvidenceFlag());
  }
}
