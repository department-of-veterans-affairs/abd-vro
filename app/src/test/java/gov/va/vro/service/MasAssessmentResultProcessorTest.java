package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.model.rrd.AbdEvidenceWithSummary;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.service.provider.camel.processor.MasAssessmentResultProcessor;
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
    String benefitClaimId = "111";
    String collectionId = "666";
    String diagnosticCode = "999";
    String idType = MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE;

    Claim claim = new Claim();

    claim.setBenefitClaimId(benefitClaimId);
    claim.setIdType(idType);
    claim.setVeteranIcn("v1");
    claim.setDiagnosticCode(diagnosticCode);
    claim.setCollectionId(collectionId);
    saveToDbService.insertClaim(claim);

    // IdType on evidence is set by the processor since it doesn't come in otherwise.
    // This is why the below line of exchange getProperty is important to mock.
    var evidence = new AbdEvidenceWithSummary();
    evidence.setClaimSubmissionId(collectionId);
    evidence.setEvidenceSummary(Map.of("Hello", 10));

    var message = Mockito.mock(Message.class);
    var exchange = Mockito.mock(Exchange.class);
    Mockito.when(exchange.getMessage()).thenReturn(message);
    Mockito.when(exchange.getProperty("diagnosticCode", String.class)).thenReturn(diagnosticCode);
    Mockito.when(exchange.getProperty("idType", String.class)).thenReturn(idType);

    Mockito.when(message.getBody(AbdEvidenceWithSummary.class)).thenReturn(evidence);
    processor.process(exchange);

    claimSubmissionRepository
        .findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(collectionId, idType)
        .orElseThrow();
    var results =
        assessmentResultRepository.findAll().stream()
            .filter(result -> diagnosticCode.equals(result.getContention().getDiagnosticCode()))
            .toList();
    assertEquals(1, results.size());
    var result = results.get(0);
    var summary = result.getEvidenceCountSummary();
    assertEquals("10", summary.get("Hello"));
  }
}
