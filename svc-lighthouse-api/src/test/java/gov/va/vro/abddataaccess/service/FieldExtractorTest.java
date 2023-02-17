package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.EncodingEnum;
import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Unit tests for FieldExtractor.
 *
 * @author warren @Date 8/30/22
 */
@Slf4j
class FieldExtractorTest {

  private static final String CONTENT_TYPE = "application/fhir+json";
  private static final String MEDICATION_REQUEST_RESPONSE = "medication-response-bundle.json";
  private static final String OBSERVATION_RESPONSE = "observation-response-bundle.json";
  private static final String PROCEDURE_RESPONSE = "procedure-response-bundle.json";
  private static final String CONDITION_RESPONSE = "condition-response-bundle.json";
  private static final String TEST = "bloodpresure-measurement-test.json";

  private IParser parser;

  @BeforeEach
  void setUp() {
    FhirContext fhirContext = FhirContext.forR4();
    EncodingEnum respType = EncodingEnum.forContentType(CONTENT_TYPE);
    parser = respType.newParser(fhirContext);
  }

  @Test
  public void testExtractCondition() {
    log.info("test ExtractCondition");

    try {
      Bundle retVal = getBundle(CONDITION_RESPONSE);
      List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
      assertTrue(entries.size() > 1);

      entries.parallelStream().forEach(this::verifyCondition);
    } catch (Exception e) {
      log.error("testExtractCondition error: {}", e.getMessage(), e);
      fail("testExtractCondition.");
    }
  }

  @Test
  public void testExtractMedication() {
    log.info("test testExtractMedication");
    try {
      Bundle retVal = getBundle(MEDICATION_REQUEST_RESPONSE);
      List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
      assertTrue(entries.size() > 1);

      entries.parallelStream().forEach(this::verifyAbdMedication);
    } catch (Exception e) {
      log.error("testExtractMedication error: {}", e.getMessage(), e);
      fail("text extractmedication.");
    }
  }

  @Test
  public void testExtractProcedure() {
    log.info("test ExtractProcedure");
    try {
      Bundle retVal = getBundle(PROCEDURE_RESPONSE);
      List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
      assertTrue(entries.size() > 1);

      entries.parallelStream().forEach(this::verifyProcedure);
    } catch (Exception e) {
      log.error("ExtractProcedure error: {}", e.getMessage(), e);
      fail("text ExtractProcedure.");
    }
  }

  @Test
  public void testExtractBloodPressure() {
    log.info("test ExtractBloodPressure");
    try {
      Bundle retVal = getBundle(OBSERVATION_RESPONSE);
      List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
      assertTrue(entries.size() > 1);

      entries.parallelStream().forEach(this::verifyBloodPressure);
    } catch (Exception e) {
      log.error("testExtractBloodPressure error: {}", e.getMessage(), e);
      fail("testExtractBloodPressure.");
    }
  }

  @Test
  public void testBloodPressureMeasurement() {
    log.info("test BloodPressureMeasurement");
    try {
      Bundle bundle = getBundle(TEST);
      List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
      bundle
          .getEntry()
          .forEach(
              e -> {
                Observation o = (Observation) e.getResource();
                o.getComponent()
                    .forEach(
                        c -> {
                          if (!c.hasValueQuantity()) {
                            Quantity quantity = new Quantity();
                            quantity.setUnit("mm[Hg]");
                            String code = c.getCode().getCodingFirstRep().getCode();
                            if (code.equals("8462-4")) {
                              quantity.setValue(80);
                              c.setValue(quantity);
                            } else if (code.equals("8480-6")) {
                              quantity.setValue(120);
                              c.setValue(quantity);
                            }
                          }
                        });
              });
      entries.parallelStream().forEach(this::verifyBloodPressure);
    } catch (Exception e) {
      log.error("testBloodPressureMeasurement error: {}", e.getMessage(), e);
      fail("testBloodPressureMeasurement.");
    }
  }

  private Bundle getBundle(String conditionResponse) throws IOException {
    String testfile =
        Objects.requireNonNull(getClass().getClassLoader().getResource(conditionResponse))
            .getPath();
    File initialFile = new File(testfile);
    InputStream theResponseInputStream = new FileInputStream(initialFile);
    Bundle retVal = parser.parseResource(Bundle.class, theResponseInputStream);
    theResponseInputStream.close();
    return retVal;
  }

  private void verifyAbdMedication(Bundle.BundleEntryComponent entry) {
    MedicationRequest resource = (MedicationRequest) entry.getResource();
    AbdMedication abdMedication = FieldExtractor.extractMedication(resource);
    assertEquals(ResourceType.MedicationRequest, resource.getResourceType());
    assertEquals(resource.getStatus().toCode(), abdMedication.getStatus().toLowerCase());
    assertEquals(resource.hasNote(), !abdMedication.getNotes().isEmpty());
    assertEquals(resource.hasAuthoredOn(), !abdMedication.getAuthoredOn().isBlank());
    assertEquals(resource.hasMedicationReference(), !abdMedication.getDescription().isBlank());
  }

  private void verifyBloodPressure(Bundle.BundleEntryComponent entry) {
    Observation resource = (Observation) entry.getResource();
    AbdBloodPressure abdBloodPressure = FieldExtractor.extractBloodPressure(resource);
    assertEquals(ResourceType.Observation, resource.getResourceType());
    assertEquals(resource.hasEffectiveDateTimeType(), abdBloodPressure.getDate() != null);
    // test blood presure reading
    if (resource.hasPerformer()) {
      List<String> references =
          resource.getPerformer().stream()
              .filter(p -> p.hasReference() & p.hasDisplay())
              .map(Reference::getReference)
              .collect(Collectors.toList());
      if (!references.isEmpty()) {
        boolean hasPractitioner = references.stream().anyMatch(r -> r.contains("Practitioner"));
        boolean hasOrganization = references.stream().anyMatch(r -> r.contains("Organization"));
        if (hasPractitioner) {
          assertFalse(abdBloodPressure.getPractitioner().isEmpty());
        }
        if (hasOrganization) {
          assertFalse(abdBloodPressure.getOrganization().isEmpty());
        }
      }
    }
  }

  private void verifyProcedure(Bundle.BundleEntryComponent entry) {
    Procedure procedure = (Procedure) entry.getResource();
    assertEquals(ResourceType.Procedure, procedure.getResourceType());
    AbdProcedure abdProcedure = FieldExtractor.extractProcedure(procedure);
    assertEquals(
        procedure.hasStatus(), !Optional.ofNullable(abdProcedure.getStatus()).orElse("").isEmpty());
    assertEquals(
        procedure.hasCode(), !Optional.ofNullable(abdProcedure.getCode()).orElse("").isEmpty());
  }

  private void verifyCondition(Bundle.BundleEntryComponent entry) {
    Condition condition = (Condition) entry.getResource();
    assertEquals(ResourceType.Condition, condition.getResourceType());
    AbdCondition abdCondition = FieldExtractor.extractCondition(condition);
    log.info("abdCondition: {}", abdCondition.getText());
    if (condition.hasCode()) {
      CodeableConcept codeableConcept = condition.getCode();
      if (codeableConcept.hasCoding()) {
        Coding coding = codeableConcept.getCodingFirstRep();
        assertEquals(
            Optional.ofNullable(coding.getCode()).orElse(""),
            Optional.ofNullable(abdCondition.getCode()).orElse(""));
        if (coding.hasDisplay()) {
          assertEquals(coding.getDisplay(), abdCondition.getText());
        }
      }
    }
    if (condition.hasAbatementDateTimeType()) {
      assertEquals(
          Optional.ofNullable(condition.getAbatementDateTimeType().asStringValue())
              .orElse("")
              .isEmpty(),
          abdCondition.getAbatementDate().isEmpty());
    }
    if (condition.hasOnsetDateTimeType()) {
      assertEquals(
          Optional.ofNullable(condition.getOnsetDateTimeType().asStringValue())
              .orElse("")
              .isEmpty(),
          abdCondition.getOnsetDate().isEmpty());
    }
    if (condition.hasRecordedDateElement()) {
      assertEquals(
          Optional.ofNullable(condition.getRecordedDateElement().asStringValue())
              .orElse("")
              .isEmpty(),
          abdCondition.getRecordedDate().isEmpty());
    }
  }
}
