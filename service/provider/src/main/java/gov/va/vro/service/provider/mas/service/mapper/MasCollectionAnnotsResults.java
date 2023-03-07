package gov.va.vro.service.provider.mas.service.mapper;

import static java.util.Objects.isNull;

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

  private static final String DATA_SOURCE = "MAS";
  private static final String UTC_TM = "T00:00:00Z";
  private static final String NOT_AVAILABLE_STR = "N/A";
  private static final String BP_CONDITION = "Hypertension";
  private static final String ASTHMA_CONDITION = "Asthma";
  private static final String BP_SYSTOLIC_CODE = "8480-6";
  private static final String BP_SYSTOLIC_DISPLAY = "Systolic blood pressure";
  private static final String BP_DIASTOLIC_CODE = "8462-4";
  private static final String BP_DIASTOLIC_DISPLAY = "Diastolic blood pressure";
  private static final String BP_UNIT = "mm[Hg]";


  /**
   * Maps annotations to evidence.
   *
   * @param masCollectionAnnotation annotation
   * @return abd evidence
   */
  public AbdEvidence mapAnnotationsToEvidence(MasCollectionAnnotation masCollectionAnnotation) {

    List<AbdMedication> medications = new ArrayList<>();
    List<AbdCondition> conditions = new ArrayList<>();
    List<AbdBloodPressure> bpReadings = new ArrayList<>();
    List<ServiceLocation> serviceLocations = new ArrayList<>();
    boolean isConditionBp = false;
    boolean isConditionAsthma = false;

    for (MasDocument masDocument : masCollectionAnnotation.getDocuments()) {
      isConditionBp = masDocument.getCondition().equalsIgnoreCase(BP_CONDITION);
      isConditionAsthma = masDocument.getCondition().equalsIgnoreCase(ASTHMA_CONDITION);
      if (masDocument.getAnnotations() != null) {
        String documentId = masDocument.getEfolderversionrefid();
        String receiptDate = masDocument.getRecDate();
        String source = masDocument.getDocTypeDescription();
        if (documentId == null) {
          documentId = "";
        }
        if (receiptDate == null) {
          receiptDate = "";
        } else {
          receiptDate = receiptDate.replaceAll("Z", "");
        }
        if (source == null) {
          source = "";
        }

        for (MasAnnotation masAnnotation : masDocument.getAnnotations()) {
          log.info(
                  ">>>> Annotation Type <<<<<< : {} ",
                  MasAnnotType.fromString(masAnnotation.getAnnotType().toLowerCase()));
          MasAnnotType annotationType =
                  MasAnnotType.fromString(masAnnotation.getAnnotType().toLowerCase());
          switch (annotationType) {
            case MEDICATION -> {
              AbdMedication abdMedication = createMedication(isConditionAsthma, masAnnotation);
              abdMedication.setDocument(documentId);
              abdMedication.setReceiptDate(receiptDate);
              abdMedication.setOrganization(source);
              medications.add(abdMedication);
            }
            case CONDITION -> {
              AbdCondition abdCondition = createCondition(masAnnotation);
              abdCondition.setDocument(documentId);
              abdCondition.setReceiptDate(receiptDate);
              abdCondition.setOrganization(source);
              conditions.add(abdCondition);
            }
            case LABRESULT, BLOOD_PRESSURE -> {
              AbdBloodPressure abdBloodPressure = createBloodPressure(masAnnotation);
              abdBloodPressure.setDocument(documentId);
              abdBloodPressure.setReceiptDate(receiptDate);
              abdBloodPressure.setOrganization(source);
              bpReadings.add(abdBloodPressure);
            }
            case SERVICE -> {
              ServiceLocation veteranService = createServiceLocation(masAnnotation);
              veteranService.setDocument(source);
              veteranService.setReceiptDate(receiptDate);
              veteranService.setDocumentId(documentId);
              serviceLocations.add(veteranService);
            }
            default -> { // NOP
            }
          }
        }
      }
    }
    List<AbdProcedure> procedures = new ArrayList<>();
    AbdEvidence abdEvidence = new AbdEvidence();
    abdEvidence.setMedications(medications);
    abdEvidence.setConditions(conditions);
    abdEvidence.setProcedures(procedures);
    abdEvidence.setBloodPressures(bpReadings);
    abdEvidence.setServiceLocations(serviceLocations);
    abdEvidence.setDocumentsWithoutAnnotationsChecked(
            masCollectionAnnotation.getDocumentsWithoutAnnotationsChecked());
    return abdEvidence;
  }

  private static AbdBloodPressure createBloodPressure(MasAnnotation masAnnotation) {
    String[] bpValues = masAnnotation.getAnnotVal().split("/");

    AbdBpMeasurement systolicReading = new AbdBpMeasurement();
    systolicReading.setCode(BP_SYSTOLIC_CODE);
    systolicReading.setDisplay(BP_SYSTOLIC_DISPLAY);
    if (bpValues[0].equals("-")){
      systolicReading.setValue(BigDecimal.valueOf(0));
    }
    else{
      systolicReading.setValue(new BigDecimal(bpValues[0]).setScale(1, RoundingMode.HALF_UP));
    }
    systolicReading.setUnit(BP_UNIT);

    AbdBpMeasurement diastolicReading = new AbdBpMeasurement();
    diastolicReading.setCode(BP_DIASTOLIC_CODE);
    diastolicReading.setDisplay(BP_DIASTOLIC_DISPLAY);
    if(bpValues[1].equals("-")){
      diastolicReading.setValue(BigDecimal.valueOf(0));
    }
    else{
      diastolicReading.setValue(new BigDecimal(bpValues[1]).setScale(1, RoundingMode.HALF_UP));
    }
    diastolicReading.setUnit(BP_UNIT);

    AbdBloodPressure abdBloodPressure = new AbdBloodPressure();
    abdBloodPressure.setDataSource(DATA_SOURCE);
    if (masAnnotation.getObservationDate() != null) {
      abdBloodPressure.setDate(masAnnotation.getObservationDate().replaceAll("Z", ""));
    } else {
      abdBloodPressure.setDate("");
    }
    if (masAnnotation.getPageNum() != null) {
      abdBloodPressure.setPage(masAnnotation.getPageNum());
    } else {
      abdBloodPressure.setPage("");
    }
    if (masAnnotation.getRecDate() != null) {
      abdBloodPressure.setReceiptDate(masAnnotation.getRecDate());
    } else {
      abdBloodPressure.setReceiptDate("");
    }
    if (masAnnotation.getEFolderVersionRefId() != null) {
      abdBloodPressure.setDocument(masAnnotation.getEFolderVersionRefId());
    } else {
      abdBloodPressure.setDocument("");
    }
    if (masAnnotation.getDocTypedescription() != null) {
      abdBloodPressure.setOrganization(masAnnotation.getDocTypedescription());
    } else {
      abdBloodPressure.setOrganization("");
    }
    abdBloodPressure.setSystolic(systolicReading);
    abdBloodPressure.setDiastolic(diastolicReading);
    abdBloodPressure.setOrganization(null);
    abdBloodPressure.setPractitioner(null);
    return abdBloodPressure;
  }

  private static AbdMedication createMedication(
          boolean isConditionAsthma, MasAnnotation masAnnotation) {
    AbdMedication abdMedication = new AbdMedication();
    abdMedication.setDataSource(DATA_SOURCE);
    abdMedication.setStatus(null);
    abdMedication.setNotes(null);
    abdMedication.setDescription(masAnnotation.getAnnotVal().toLowerCase());
    abdMedication.setRefills(-1);
    abdMedication.setAsthmaRelevant(null);
    abdMedication.setDuration(null);
    if (masAnnotation.getObservationDate() != null) {
      abdMedication.setAuthoredOn(masAnnotation.getObservationDate().replaceAll("Z", "") + UTC_TM);
    } else {
      abdMedication.setAuthoredOn("");
    }
    if (masAnnotation.getPartialDate() != null) {
      abdMedication.setPartialDate(masAnnotation.getPartialDate().replaceAll("Z", ""));
    } else {
      abdMedication.setPartialDate("");
    }
    if (masAnnotation.getPageNum() != null) {
      abdMedication.setPage(masAnnotation.getPageNum());
    } else {
      abdMedication.setPage("");
    }
    abdMedication.setRoute(null);
    abdMedication.setAsthmaRelevant(isConditionAsthma);
    return abdMedication;
  }

  private static AbdCondition createCondition(MasAnnotation masAnnotation) {
    AbdCondition abdCondition = new AbdCondition();
    abdCondition.setDataSource(DATA_SOURCE);
    abdCondition.setCode(masAnnotation.getAnnotVal());
    abdCondition.setText(masAnnotation.getAcdPrefName());
    abdCondition.setStatus(null);
    abdCondition.setAbatementDate(null);
    if (masAnnotation.getObservationDate() != null) {
      abdCondition.setRecordedDate(masAnnotation.getObservationDate().replaceAll("Z", ""));
    } else {
      abdCondition.setRecordedDate("");
    }
    if (masAnnotation.getPartialDate() != null) {
      abdCondition.setPartialDate(masAnnotation.getPartialDate().replaceAll("Z", ""));
    } else {
      abdCondition.setPartialDate("");
    }
    if (masAnnotation.getPageNum() != null) {
      abdCondition.setPage(masAnnotation.getPageNum());
    } else {
      abdCondition.setPage("");
    }
    return abdCondition;
  }

  private static ServiceLocation createServiceLocation(MasAnnotation masAnnotation) {
    ServiceLocation veteranService = new ServiceLocation();
    if (!isNull(masAnnotation.getAnnotVal())) {
      veteranService.setLocation(masAnnotation.getAnnotVal());
    } else {
      veteranService.setLocation(NOT_AVAILABLE_STR);
    }
    if (!isNull(masAnnotation.getPageNum())) {
      veteranService.setPage(masAnnotation.getPageNum());
    } else {
      veteranService.setPage(NOT_AVAILABLE_STR);
    }
    return veteranService;
  }
}
