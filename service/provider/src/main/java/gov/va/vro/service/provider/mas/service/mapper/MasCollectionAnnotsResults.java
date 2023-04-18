package gov.va.vro.service.provider.mas.service.mapper;

import static java.util.Objects.isNull;

import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdBpMeasurement;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import gov.va.vro.model.ServiceLocation;
import gov.va.vro.model.mas.MasAnnotType;
import gov.va.vro.model.mas.MasAnnotation;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private static final String BP_READING_REGEX = "(-|\\d+)"; // "^\\d{1,3}\\s*\\/\\s*\\d{1,3}\\s*$";
  private static final int BP_VALUE_LENGTH = 3;
  DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  DateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");

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
            case BLOOD_PRESSURE -> {
              AbdBloodPressure abdBloodPressure = createBloodPressure(masAnnotation);
              if (abdBloodPressure != null) {
                abdBloodPressure.setDocument(documentId);
                abdBloodPressure.setReceiptDate(receiptDate);
                abdBloodPressure.setOrganization(source);
                bpReadings.add(abdBloodPressure);
              }
            }
            case SERVICE -> {
              ServiceLocation veteranService = createServiceLocation(masAnnotation);
              veteranService.setDocument(source);
              String serviceReceiptDate;
              try {
                serviceReceiptDate = formatter1.format(formatter.parse(receiptDate));
              } catch (ParseException e) {
                serviceReceiptDate = receiptDate;
                log.error("Un-parsable date for ReceiptDate: {}.", receiptDate);
              }
              veteranService.setReceiptDate(serviceReceiptDate);
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

  private static BigDecimal getBpReadingValue(String valueString) {
    if (valueString.length() > BP_VALUE_LENGTH) {
      log.error("Invalid blood pressure reading value: {}.", valueString);
      return null;
    }
    if (valueString.equals("-")) { // return default value.
      return BigDecimal.valueOf(0);
    } else {
      try {
        return new BigDecimal(valueString).setScale(1, RoundingMode.HALF_UP);
      } catch (Exception e) {
        log.error("Invalid blood pressure reading value: {}", valueString);
        return null;
      }
    }
  }

  private static AbdBloodPressure createBloodPressure(MasAnnotation masAnnotation) {
    log.info("MasAnnotation: {}", masAnnotation.getAnnotVal());
    Pattern pattern = Pattern.compile(BP_READING_REGEX);
    Matcher matcher = pattern.matcher(masAnnotation.getAnnotVal());

    BigDecimal systolicVal;
    BigDecimal diastolicVal;
    if (matcher.find()) {
      systolicVal = getBpReadingValue(matcher.group());
      if (systolicVal == null) {
        return null;
      }

      if (matcher.find()) {
        String valueString = matcher.group();
        if (valueString.equals("-") && BigDecimal.ZERO.equals(systolicVal)) {
          return null; // skip empty blood pressure reading.
        }
        diastolicVal = getBpReadingValue(matcher.group());
        if (diastolicVal == null) {
          return null;
        }
      } else {
        log.info(
            "Missing blood pressure diastolic reading: {}. Default to 0.",
            masAnnotation.getAnnotVal());
        diastolicVal = BigDecimal.valueOf(0);
      }
    } else {
      log.error(
          "Missing blood pressure reading in the MAS annotation: {}.", masAnnotation.getAnnotVal());
      return null;
    }
    if (systolicVal.equals("-") && diastolicVal.equals("-")) { // skip missing BP reading values.
      return null;
    }

    AbdBpMeasurement systolicReading = new AbdBpMeasurement();
    systolicReading.setCode(BP_SYSTOLIC_CODE);
    systolicReading.setDisplay(BP_SYSTOLIC_DISPLAY);
    systolicReading.setValue(systolicVal);
    systolicReading.setUnit(BP_UNIT);
    AbdBpMeasurement diastolicReading = new AbdBpMeasurement();
    diastolicReading.setCode(BP_DIASTOLIC_CODE);
    diastolicReading.setDisplay(BP_DIASTOLIC_DISPLAY);
    diastolicReading.setValue(diastolicVal);
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
