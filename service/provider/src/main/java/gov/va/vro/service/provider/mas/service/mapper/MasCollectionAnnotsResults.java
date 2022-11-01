package gov.va.vro.service.provider.mas.service.mapper;

import gov.va.vro.model.*;
import gov.va.vro.model.mas.MasAnnotType;
import gov.va.vro.model.mas.MasAnnotation;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasCollectionAnnotsResults {

  private static final String BP_CONDITION = "Hypertension";
  private static final String ASTHMA_CONDITION = "Asthma";
  private static final String BP_SYSTOLIC_CODE = "8480-6";
  private static final String BP_SYSTOLIC_DISPLAY = "Systolic blood pressure";
  private static final String BP_DIASTOLIC_CODE = "8462-4";
  private static final String BP_DIASTOLIC_DISPLAY = "Diastolic blood pressure";
  private static final String BP_UNIT = "mm[Hg]";
  private static final String BP_READING_REGEX = "^\\d{1,3}\\/\\d{1,3}$";

  public AbdEvidence mapAnnotsToEvidence(MasCollectionAnnotation masCollectionAnnotation) {

    AbdEvidence abdEvidence = new AbdEvidence();

    List<AbdMedication> medications = new ArrayList<>();
    List<AbdCondition> conditions = new ArrayList<>();
    List<AbdProcedure> procedures = new ArrayList<>();
    List<AbdBloodPressure> bpReadings = new ArrayList<>();
    boolean isConditionBP = false;
    boolean isConditionAsthma = false;

    for (MasDocument masDocument : masCollectionAnnotation.getDocuments()) {
      isConditionBP = masDocument.getCondition().equalsIgnoreCase(BP_CONDITION);
      isConditionAsthma = masDocument.getCondition().equalsIgnoreCase(ASTHMA_CONDITION);
      if (masDocument.getAnnotations() != null) {
        for (MasAnnotation masAnnotation : masDocument.getAnnotations()) {
          log.info(
              ">>>> Annotation Tpe <<<<<< : {} ",
              MasAnnotType.fromString(masAnnotation.getAnnotType().toLowerCase()));
          MasAnnotType AnnotationType =
              MasAnnotType.fromString(masAnnotation.getAnnotType().toLowerCase());
          switch (AnnotationType) {
            case MEDICATION -> {
              AbdMedication abdMedication = new AbdMedication();
              abdMedication.setStatus(null);
              abdMedication.setNotes(null);
              abdMedication.setDescription(masAnnotation.getAnnotVal().toLowerCase());
              abdMedication.setRefills(-1);
              abdMedication.setAsthmaRelevant(null);
              abdMedication.setDuration(null);
              if (masAnnotation.getObservationDate() != null) {
                abdMedication.setAuthoredOn(masAnnotation.getObservationDate().replaceAll("Z", ""));
              } else {
                abdMedication.setAuthoredOn("9999-12-31");
              }
              abdMedication.setRoute(null);
              abdMedication.setAsthmaRelevant(isConditionAsthma);
              medications.add(abdMedication);
            }
            case CONDITION -> {
              AbdCondition abdCondition = new AbdCondition();
              abdCondition.setCode(masAnnotation.getAnnotVal());
              abdCondition.setText(masAnnotation.getAcdPrefName());
              abdCondition.setStatus(null);
              abdCondition.setAbatementDate(null);
              abdCondition.setOnsetDate(masAnnotation.getObservationDate());
              conditions.add(abdCondition);
            }
            case LABRESULT -> {
              if (isConditionBP && masAnnotation.getAnnotVal().matches(BP_READING_REGEX)) {
                String[] bpValues = masAnnotation.getAnnotVal().split("/");

                AbdBpMeasurement systolicReading = new AbdBpMeasurement();
                systolicReading.setCode(BP_SYSTOLIC_CODE);
                systolicReading.setDisplay(BP_SYSTOLIC_DISPLAY);
                systolicReading.setValue(
                    new BigDecimal(bpValues[0]).setScale(1, RoundingMode.HALF_UP));
                systolicReading.setUnit(BP_UNIT);

                AbdBpMeasurement diastolicReading = new AbdBpMeasurement();
                diastolicReading.setCode(BP_DIASTOLIC_CODE);
                diastolicReading.setDisplay(BP_DIASTOLIC_DISPLAY);
                diastolicReading.setValue(
                    new BigDecimal(bpValues[1]).setScale(1, RoundingMode.HALF_UP));
                diastolicReading.setUnit(BP_UNIT);

                AbdBloodPressure abdBloodPressure = new AbdBloodPressure();
                abdBloodPressure.setDate(masAnnotation.getObservationDate());
                abdBloodPressure.setSystolic(systolicReading);
                abdBloodPressure.setDiastolic(diastolicReading);
                abdBloodPressure.setOrganization(null);
                abdBloodPressure.setPractitioner(null);
                bpReadings.add(abdBloodPressure);
              }
            }
            case SERVICE, PROCEDURE -> { // NOP
            }
            default -> { // NOP
            }
          }
        }
      }
    }

    abdEvidence.setMedications(medications);
    abdEvidence.setConditions(conditions);
    abdEvidence.setProcedures(procedures);
    abdEvidence.setBloodPressures(bpReadings);

    return abdEvidence;
  }
}
