package gov.va.vro.abd_data_access.service;

import gov.va.vro.abd_data_access.model.*;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FieldExtractor {
  private static String toDate(DateTimeType dateTimeType) {
    String value = dateTimeType.asStringValue();
    if (value == null) return "";
    int index = value.indexOf('T');
    if (index > 0) {
      return value.substring(0, index);
    }
    return value;
  }

  private static String formatDate(Date date) {
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return fmt.format(date);
  }

  public static AbdCondition extractCondition(Condition condition) {
    AbdCondition result = new AbdCondition();

    if (condition.hasCode()) {
      CodeableConcept code = condition.getCode();
      if (code.hasCoding()) {
        Coding coding = code.getCodingFirstRep();

        if (coding.hasCode()) {
          result.setCode(coding.getCode());
        }

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

    return result;
  }

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

  private static AbdBPMeasurement extractBPMeasurement(
      Coding coding, ObservationComponentComponent component) {
    AbdBPMeasurement result = new AbdBPMeasurement();

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
                AbdBPMeasurement m = extractBPMeasurement(codingInner, component);
                result.setSystolic(m);
              }
              if ("8462-4".equals(bpType)) {
                AbdBPMeasurement m = extractBPMeasurement(codingInner, component);
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
