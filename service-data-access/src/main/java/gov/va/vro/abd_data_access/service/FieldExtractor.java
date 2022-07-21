package gov.va.vro.abd_data_access.service;

import gov.va.vro.abd_data_access.model.AbdBPMeasurement;
import gov.va.vro.abd_data_access.model.AbdBloodPressure;
import gov.va.vro.abd_data_access.model.AbdCondition;
import gov.va.vro.abd_data_access.model.AbdMedication;
import gov.va.vro.abd_data_access.model.AbdProcedure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;

public class FieldExtractor {
    static private String toDate(DateTimeType dateTimeType) {
        String value = dateTimeType.asStringValue();
        int index = value.indexOf('T');
        if (index > 0) {
            return value.substring(0, index);
        }
        return value;
    }

    static public AbdCondition extractCondition(Condition condition) {
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

    static public AbdMedication extractMedication(MedicationRequest medication) {
        AbdMedication result = new AbdMedication();

        if (medication.hasMedicationCodeableConcept()) {
            CodeableConcept code = medication.getMedicationCodeableConcept();
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

            if (medication.hasAuthoredOn()) {
                result.setDate(FieldExtractor.toDate(medication.getAuthoredOnElement()));
            }

            if (medication.hasStatus()) {
                result.setStatus(medication.getStatus().getDisplay());
            }

            if (medication.hasDispenseRequest()) {
                MedicationRequestDispenseRequestComponent dr = medication.getDispenseRequest();
                if (dr.hasNumberOfRepeatsAllowed()) {
                    result.setRefills(dr.getNumberOfRepeatsAllowed());
                }
                if (dr.hasExpectedSupplyDuration()) {
                    Duration duration = dr.getExpectedSupplyDuration();
                    String d = duration.getValue().toString() + " " + duration.getUnit();
                    result.setDuration(d); 
                }
            }

            if (medication.hasNote()) {
                List<String> notes = medication.getNote().stream().map(annotation -> annotation.getText()).toList();
                result.setNotes(notes);
            }
        }

        return result;
    }

    static public AbdProcedure extractProcedure(Procedure procedure) {
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

    static private AbdBPMeasurement extractBPMeasurement(Coding coding, ObservationComponentComponent component) {
        AbdBPMeasurement result = new AbdBPMeasurement();

        result.setCode(coding.getCode());
        result.setDisplay(coding.getDisplay());

        Quantity quantity = component.getValueQuantity();

        result.setUnit(quantity.getUnit());
        
        BigDecimal value = quantity.getValue().setScale(1, RoundingMode.HALF_UP);
        result.setValue(value);

        return result;
    }

    static public AbdBloodPressure extractBloodPressure(Observation observation) {
        AbdBloodPressure result = new AbdBloodPressure();

        if (observation.hasEffectiveDateTimeType()) {
            result.setDate(toDate(observation.getEffectiveDateTimeType()));
        }

        if (observation.hasComponent()) {
            for (ObservationComponentComponent component: observation.getComponent()) {
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
                            if("8462-4".equals(bpType)) {
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
            performers.forEach(reference -> {
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
