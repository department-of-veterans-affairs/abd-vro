package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.*;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MasCollectionServiceTest {

  @Test
  void combineEvidence() {
    var lighthouseAssessment = createAssessment("123");
    lighthouseAssessment.setSource(HealthAssessmentSource.LIGHTHOUSE);
    lighthouseAssessment.setEvidence(
        createEvidence(
            Arrays.asList(createMedication("med1"), createMedication("med2")),
            Collections.singletonList(createCondition("cond2"))));
    var masAssessment = createAssessment(null);
    masAssessment.setSource(HealthAssessmentSource.MAS);
    masAssessment.setDisabilityActionType("INCREASE");
    masAssessment.setEvidence(
        createEvidence(
            Collections.singletonList(createMedication("med1")),
            Collections.singletonList(createCondition("cond1"))));
    var result = MasCollectionService.combineEvidence(lighthouseAssessment, masAssessment);
    assertEquals("INCREASE", result.getDisabilityActionType());
    assertEquals("12345", result.getDiagnosticCode());
    assertEquals("icn", result.getVeteranIcn());
    assertEquals("123", result.getClaimSubmissionId());
    var evidence = result.getEvidence();
    assertTrue(evidence.getBloodPressures().isEmpty());
    assertTrue(evidence.getProcedures().isEmpty());
    assertEquals(2, evidence.getMedications().size());
    assertEquals(2, evidence.getConditions().size());
  }

  private AbdCondition createCondition(String code) {
    var condition = new AbdCondition();
    condition.setCode(code);
    condition.setText("text");
    return condition;
  }

  private AbdMedication createMedication(String route) {
    var medication = new AbdMedication();
    medication.setRoute(route);
    medication.setDescription("medication");
    return medication;
  }

  private AbdEvidence createEvidence(
      List<AbdMedication> medications, List<AbdCondition> conditions) {
    var evidence = new AbdEvidence();
    evidence.setMedications(medications);
    evidence.setConditions(conditions);
    return evidence;
  }

  private HealthDataAssessment createAssessment(String claimId) {
    var assessment = new HealthDataAssessment();
    assessment.setClaimSubmissionId(claimId);
    assessment.setDiagnosticCode("12345");
    assessment.setVeteranIcn("icn");
    return assessment;
  }
}
