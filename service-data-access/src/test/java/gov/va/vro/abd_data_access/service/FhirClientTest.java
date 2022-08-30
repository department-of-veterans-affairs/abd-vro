package gov.va.vro.abd_data_access.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.EncodingEnum;
import gov.va.vro.abd_data_access.exception.AbdException;
import gov.va.vro.abd_data_access.model.AbdBloodPressure;
import gov.va.vro.abd_data_access.model.AbdClaim;
import gov.va.vro.abd_data_access.model.AbdEvidence;
import gov.va.vro.abd_data_access.model.AbdMedication;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FhirClient.
 *
 * @author warren
 * @Date 8/30/22
 */

@ExtendWith(MockitoExtension.class)
@Slf4j
class FhirClientTest {

    private static final String TEST_PATIENT = "9000682";
    private static final String TEST_DIAGNOSTIC_CODE = "7101";
    private static final String TEST_MEDICATION_REQUEST = "6602";
    private static final String TEST_CLAIM_ID = "1234";

    private final static String CONTENT_TYPE = "application/fhir+json";
    private final static String MEDICATION_REQUEST_RESPONSE = "medication-response-bundle.json";
    private final static String OBSERVATION_RESPONSE = "observation-response-bundle.json";

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 30;

    @Spy
    private FhirClient client = new FhirClient();

    private IParser parser;
    private Bundle medicationBundle, bpBundle;

    @BeforeEach
    void setUp() throws Exception {
        FhirContext fhirContext = FhirContext.forR4();
        EncodingEnum respType = EncodingEnum.forContentType(CONTENT_TYPE);
        parser = respType.newParser(fhirContext);

        String testfile = getClass().getClassLoader().getResource(MEDICATION_REQUEST_RESPONSE).getPath();
        medicationBundle = getBundle(testfile);

        testfile = getClass().getClassLoader().getResource(OBSERVATION_RESPONSE).getPath();
        bpBundle = getBundle(testfile);
    }

    private void mockGetBundle(Bundle bundle, AbdDomain domain) throws AbdException {
        Mockito.doReturn(bundle)
                .when(client).getBundle(domain,
                TEST_PATIENT,
                DEFAULT_PAGE,
                DEFAULT_SIZE);
        if (bundle.hasEntry()) {
            Mockito.doReturn(new Bundle())
                    .when(client).getBundle(domain,
                    TEST_PATIENT,
                    DEFAULT_PAGE + 1,
                    DEFAULT_SIZE);
        }
    }

    @Test
    public void testGetMedicalEvidence() {
        AbdClaim testClaim = new AbdClaim();
        testClaim.setClaimSubmissionId(TEST_CLAIM_ID);
        testClaim.setDiagnosticCode(TEST_MEDICATION_REQUEST);
        testClaim.setVeteranIcn(TEST_PATIENT);
        try {
            mockGetBundle(medicationBundle, AbdDomain.MEDICATION);
            mockGetBundle(new Bundle(), AbdDomain.CONDITION);
            AbdEvidence evidence = client.getMedicalEvidence(testClaim);
            assertNotNull(evidence);
            assertTrue(evidence.getMedications().size() > 0);
            assertEquals(evidence.getMedications().size(),
                    medicationBundle.getEntry().size());
        } catch (Exception e) {
            log.error("testGetMedicalEvidence error: {}", e.getMessage(), e);
            fail("testGetMedicalEvidence");
        }
    }

    @Test
    public void testGetBloodPressure() {
        AbdClaim testClaim = new AbdClaim();
        testClaim.setClaimSubmissionId(TEST_CLAIM_ID);
        testClaim.setDiagnosticCode(TEST_DIAGNOSTIC_CODE);
        testClaim.setVeteranIcn(TEST_PATIENT);
        try {
            mockGetBundle(bpBundle, AbdDomain.BLOOD_PRESSURE);
            mockGetBundle(medicationBundle, AbdDomain.MEDICATION);
            AbdEvidence evidence = client.getMedicalEvidence(testClaim);
            assertNotNull(evidence);
            assertTrue(evidence.getBloodPressures().size() > 0);
            assertEquals(evidence.getBloodPressures().size(),
                    bpBundle.getEntry().size());
        } catch (Exception e) {
            log.error("testGetBloodPressure error: {}", e.getMessage(), e);
            fail("testGetBloodPressure");
        }
    }

    @Test
    public void testGetAbdEvidence() {
        Map<AbdDomain, List<Bundle.BundleEntryComponent>> domainBundles = new HashMap<>();
        try {
            String testfile = getClass().getClassLoader().getResource(MEDICATION_REQUEST_RESPONSE).getPath();
            Bundle bundle = getBundle(testfile);
            assertNotNull(bundle);
            domainBundles.put(AbdDomain.MEDICATION, bundle.getEntry());

            testfile = getClass().getClassLoader().getResource(OBSERVATION_RESPONSE).getPath();
            bundle = getBundle(testfile);
            assertNotNull(bundle);
            domainBundles.put(AbdDomain.BLOOD_PRESSURE, bundle.getEntry());

            AbdEvidence evidence = client.getAbdEvidence(domainBundles);
            assertNotNull(evidence);
            List<AbdMedication> medications = evidence.getMedications();
            assertNotNull(medications);
            List<AbdBloodPressure> bp = evidence.getBloodPressures();
            assertNotNull(bp);
        } catch (Exception e) {
            log.error("testGetAbdEvidence error: {}", e.getMessage(), e);
            fail("testGetAbdEvidence");
        }
    }

    private Bundle getBundle(String filename) {
        try {
            File initialFile = new File(filename);
            InputStream theResponseInputStream = new FileInputStream(initialFile);
            Bundle retVal = parser.parseResource(Bundle.class, theResponseInputStream);
            theResponseInputStream.close();
            return retVal;
        } catch (Exception e) {
            log.error("getBundle from {} failed. {} ", filename, e.getMessage(), e);
            return null;
        }
    }

    @AfterEach
    void tearDown() {
    }
}