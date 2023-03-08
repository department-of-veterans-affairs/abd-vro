package gov.va.vro.abddataaccess.service;

import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdBpMeasurement;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/** It contains the functions to extract data from FHIR objects for Abd data models. */
public class FieldExtractor {

  private static final String SNOMED = "http://snomed.info";
  private static final String MISSING = "*Missing*";

  private static String toDate(DateTimeType dateTimeType) {
    String value = dateTimeType.asStringValue();
    if (value == null) {
      return "";
    }
    int index = value.indexOf('T');
    if (index > 0) {
      return value.substring(0, index);
    }
    return value;
  }

  /**
   * Converts a date to a string with the specified format.
   *
   * @param date a date.
   * @return a string.
   */
  private static String formatDate(Date date) {
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return fmt.format(date);
  }

  public static Coding getAValidCoding(CodeableConcept code) {
    if (code.hasCoding()) {
      for (Coding coding : code.getCoding()) {
        if (coding.hasCode()
            && !coding.getSystem().trim().startsWith(SNOMED)
            && !coding.getCode().startsWith(MISSING)) {
          return coding;
        }
      }
    }
    return null;
  }

  /**
   * Creates an {@link AbdCondition} from a Fhir {@link Condition}.
   *
   * @param condition a Fhir Condition object.
   * @return an AbdCondition object.
   */
  public static AbdCondition extractCondition(Condition condition) {
    AbdCondition result = new AbdCondition();

    if (condition.hasCode()) {
      CodeableConcept code = condition.getCode();
      Coding coding = getAValidCoding(code);
      if (coding != null) {
        result.setCode(coding.getCode());
        if (coding.hasDisplay()) {
          result.setText(coding.getDisplay());
        }
      }

      String textFound = result.getText();

      if ((textFound == null || textFound.isEmpty()) && code.hasText()) {
        result.setText(code.getText());
      }
    }

    if (condition.hasAbatementDateTimeType()) {
      result.setAbatementDate(FieldExtractor.toDate(condition.getAbatementDateTimeType()));
    }
    if (condition.hasOnsetDateTimeType()) {
      result.setOnsetDate(FieldExtractor.toDate(condition.getOnsetDateTimeType()));
    }
    if (condition.hasRecordedDateElement()) {
      result.setRecordedDate(FieldExtractor.toDate(condition.getRecordedDateElement()));
    }

    if (condition.hasClinicalStatus()) {
      CodeableConcept clinicalStatus = condition.getClinicalStatus();
      if (clinicalStatus.hasCoding()) {
        Coding coding = clinicalStatus.getCodingFirstRep();
        if (coding.hasCode()) {
          String code = coding.getCode();
          result.setStatus(code);
        }
      }
    }

    if (condition.hasCategory()) {
      List<CodeableConcept> conditionCategory = condition.getCategory();
      if (conditionCategory.size() == 1) {
        CodeableConcept category = condition.getCategory().get(0);
        if (category.hasText()) {
          String text = category.getText();
          result.setCategory(text);
        }
      }
    } else {
      result.setCategory("");
    }

    return result;
  }

  /**
   * Creates an {@link AbdMedication} object from the given {@link MedicationRequest}.
   *
   * @param medication a {@link MedicationRequest} object.
   * @return an {@link AbdMedication} object.
   */
  public static AbdMedication extractMedication(MedicationRequest medication) {
    AbdMedication result = new AbdMedication();

    if (medication.hasMedicationReference()) {
      result.setDescription(medication.getMedicationReference().getDisplay());
    }
    if (medication.hasAuthoredOn()) {
      result.setAuthoredOn(formatDate(medication.getAuthoredOn()));
    }
    if (medication.hasStatus()) {
      result.setStatus(medication.getStatus().getDisplay());
    }
    if (medication.hasNote()) {
      result.setNotes(
          medication.getNote().stream()
              .filter(Annotation::hasText)
              .map(Annotation::getText)
              .collect(Collectors.toList()));
    }
    if (medication.hasDosageInstruction()) {
      List<String> dosages =
          medication.getDosageInstruction().parallelStream()
              .filter(Dosage::hasText)
              .map(Dosage::getText)
              .collect(Collectors.toList());
      List<String> codeText =
          medication.getDosageInstruction().stream()
              .filter(Dosage::hasTiming)
              .filter(t -> t.getTiming().hasCode())
              .filter(c -> c.getTiming().getCode().hasText())
              .map(c -> c.getTiming().getCode().getText())
              .collect(Collectors.toList());
      dosages.addAll(codeText);
      result.setDosageInstructions(dosages);
      String routes =
          medication.getDosageInstruction().stream()
              .filter(d -> d.hasRoute() && d.getRoute().hasText())
              .map(d -> d.getRoute().getText())
              .findFirst()
              .orElse(""); // Take 1st one for now
      result.setRoute(routes);
    }
    if (medication.hasDispenseRequest()) {
      if (medication.getDispenseRequest().hasExpectedSupplyDuration()) {
        result.setDuration(
            medication.getDispenseRequest().getExpectedSupplyDuration().getDisplay());
      }
      if (medication.getDispenseRequest().hasNumberOfRepeatsAllowed()) {
        result.setRefills(medication.getDispenseRequest().getNumberOfRepeatsAllowed());
      }
    }

    return result;
  }

  /**
   * Creates an AbdProcedure object from a given Fhir Procedure object.
   *
   * @param procedure a {@link Procedure}.
   * @return a {@link AbdProcedure}.
   */
  public static AbdProcedure extractProcedure(Procedure procedure) {
    AbdProcedure result = new AbdProcedure();

    if (procedure.hasCode()) {
      CodeableConcept code = procedure.getCode();
      if (code.hasCoding()) {
        Coding coding = code.getCodingFirstRep();

        if (coding.hasCode()) {
          result.setCode(coding.getCode());
        }

        if (coding.hasDisplay()) {
          result.setText(coding.getDisplay());
        }

        if (coding.hasSystem()) {
          result.setCodeSystem(coding.getSystem());
        }
      }

      if (result.getText() == null && code.hasText()) {
        result.setText(code.getText());
      }
    }

    if (procedure.hasStatus()) {
      ProcedureStatus status = procedure.getStatus();
      result.setStatus(status.getDisplay());
    }

    if (procedure.hasPerformedDateTimeType()) {
      DateTimeType dateTime = procedure.getPerformedDateTimeType();
      result.setPerformedDate(FieldExtractor.toDate(dateTime));
    }

    return result;
  }

  private static AbdBpMeasurement extractBpMeasurement(
      Coding coding, ObservationComponentComponent component) {
    AbdBpMeasurement result = new AbdBpMeasurement();

    result.setCode(coding.getCode());
    result.setDisplay(coding.getDisplay());

    if (component.hasValueQuantity()) {
      Quantity quantity = component.getValueQuantity();

      result.setUnit(quantity.getUnit());

      if (quantity.hasValue()) {
        BigDecimal value = quantity.getValue().setScale(1, RoundingMode.HALF_UP);
        result.setValue(value);
      }
    }

    return result;
  }

  /**
   * Creates an AbdBloodPressure object from a Fhir Observation object.
   *
   * @param observation an {@link Observation}.
   * @return an {@link AbdBloodPressure}.
   */
  public static AbdBloodPressure extractBloodPressure(Observation observation) {
    AbdBloodPressure result = new AbdBloodPressure();

    if (observation.hasEffectiveDateTimeType()) {
      result.setDate(toDate(observation.getEffectiveDateTimeType()));
    }

    if (observation.hasComponent()) {
      for (ObservationComponentComponent component : observation.getComponent()) {
        if (component.hasCode() && component.hasValueQuantity()) {
          CodeableConcept codeableConcept = component.getCode();
          if (codeableConcept.hasCoding()) {
            Coding codingInner = codeableConcept.getCodingFirstRep();
            if (codingInner.hasCode()) {
              String bpType = codingInner.getCode();
              if ("8480-6".equals(bpType)) {
                AbdBpMeasurement m = extractBpMeasurement(codingInner, component);
                result.setSystolic(m);
              }
              if ("8462-4".equals(bpType)) {
                AbdBpMeasurement m = extractBpMeasurement(codingInner, component);
                result.setDiastolic(m);
              }
            }
          }
        }
      }
    }

    if (observation.hasPerformer()) {
      List<Reference> performers = observation.getPerformer();
      performers.forEach(
          reference -> {
            if (reference.hasReference() && reference.hasDisplay()) {
              String key = reference.getReference();
              if (key.contains("Practitioner")) {
                result.setPractitioner(reference.getDisplay());
              } else if (key.contains("Organization")) {
                result.setOrganization(reference.getDisplay());
              }
            }
          });
    }

    return result;
  }
}
