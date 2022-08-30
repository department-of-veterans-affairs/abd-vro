package gov.va.vro.abd_data_access.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.EncodingEnum;
import gov.va.vro.abd_data_access.model.AbdBloodPressure;
import gov.va.vro.abd_data_access.model.AbdCondition;
import gov.va.vro.abd_data_access.model.AbdMedication;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FieldExtractor.
 *
 * @author warren
 * @Date 8/30/22
 */

@Slf4j
class FieldExtractorTest {

    private final static String TEST_CODE = "test";
    private final static String CONTENT_TYPE = "application/fhir+json";
    private final static String MEDICATION_REQUEST_RESPONSE = "medication-response-bundle.json";
    private final static String OBSERVATION_RESPONSE = "observation-response-bundle.json";

    private IParser parser;

    @BeforeEach
    void setUp() {
        FhirContext fhirContext = FhirContext.forR4();
        EncodingEnum respType = EncodingEnum.forContentType(CONTENT_TYPE);
        parser = respType.newParser(fhirContext);
    }

    @Test
    public void testExtractCondition() {
        // TODO: Revisit this when Condition is added.
        //  (It is not planned to include condition for current version.)
        System.out.println("test ExtractCondition");
        Condition testCondition = new Condition();
        CodeableConcept code = new CodeableConcept();
        code.setText(TEST_CODE);
        testCondition.setCode(code);
        AbdCondition abdCondition = FieldExtractor.extractCondition(testCondition);
        assertEquals(TEST_CODE, abdCondition.getText());
    }

    @Test
    public void testExtractMedication() {
        try {
            String testfile = getClass().getClassLoader().getResource(MEDICATION_REQUEST_RESPONSE).getPath();
            File initialFile = new File(testfile);
            InputStream theResponseInputStream = new FileInputStream(initialFile);
            Bundle retVal = parser.parseResource(Bundle.class, theResponseInputStream);
            theResponseInputStream.close();
            List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
            assertTrue(entries.size() > 1);

            entries.parallelStream().forEach(e -> verifyAbdMedication(e));
        } catch (Exception e) {
            log.error("testExtractMedication error: {}", e.getMessage());
            fail("text extractmedication.");
        }
    }

    @Test
    public void testExtractProcedure() {
        // TODO: Revisit this when Procedure is added.
        //  (It is not planned to include procedure for current version.)
    }

    @Test
    public void testExtractBPMeasurement() {
        // TODO: Revisit this when it is needed.
    }

    @Test
    public void testExtractBloodPressure() {
        try {
            String testfile = getClass().getClassLoader().getResource(OBSERVATION_RESPONSE).getPath();
            File initialFile = new File(testfile);
            InputStream theResponseInputStream = new FileInputStream(initialFile);
            Bundle retVal = parser.parseResource(Bundle.class, theResponseInputStream);
            theResponseInputStream.close();
            List<Bundle.BundleEntryComponent> entries = retVal.getEntry();
            assertTrue(entries.size() > 1);

            entries.parallelStream().forEach(e -> verifyBloodPressure(e));
        } catch (Exception e) {
            log.error("testExtractBloodPressure error: {}", e.getMessage(), e);
            fail("testExtractBloodPressure.");
        }
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
            List<String> references = resource.getPerformer().stream()
                    .filter(p -> p.hasReference() & p.hasDisplay())
                    .map(p -> p.getReference()).collect(Collectors.toList());
            if (!references.isEmpty()) {
                boolean hasPractitioner = references.stream()
                        .anyMatch(r -> r.contains("Practitioner"));
                boolean hasOrganization = references.stream()
                        .anyMatch(r -> r.contains("Organization"));
                if (hasPractitioner) {
                    assertTrue(!abdBloodPressure.getPractitioner().isEmpty());
                }
                if (hasOrganization) {
                    assertTrue(!abdBloodPressure.getOrganization().isEmpty());
                }
            }
        }
    }
}