package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MasCollectionServiceTest {

  @Test
  void combineEvidence() {
    var lighthouseAssessment = createAssessment();
    lighthouseAssessment.setEvidence(
        createEvidence(
            Arrays.asList(createMedication("med1"), createMedication("med2")),
            Collections.singletonList(createCondition("cond2"))));
    var masAssessment = createAssessment();
    masAssessment.setEvidence(
        createEvidence(
            Collections.singletonList(createMedication("med1")),
            Collections.singletonList(createCondition("cond1"))));
    var result = MasCollectionService.combineEvidence(lighthouseAssessment, masAssessment);
    assertEquals("D", result.getDisabilityActionType());
    assertEquals("12345", result.getDiagnosticCode());
    assertEquals("icn", result.getVeteranIcn());
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

  private HealthDataAssessment createAssessment() {
    var assessment = new HealthDataAssessment();
    assessment.setDisabilityActionType("D");
    assessment.setDiagnosticCode("12345");
    assessment.setVeteranIcn("icn");
    return assessment;
  }
}
