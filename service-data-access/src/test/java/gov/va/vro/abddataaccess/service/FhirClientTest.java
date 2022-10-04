package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.EncodingEnum;
import gov.va.vro.abddataaccess.exception.AbdException;
import gov.va.vro.abddataaccess.model.AbdBloodPressure;
import gov.va.vro.abddataaccess.model.AbdClaim;
import gov.va.vro.abddataaccess.model.AbdCondition;
import gov.va.vro.abddataaccess.model.AbdEvidence;
import gov.va.vro.abddataaccess.model.AbdMedication;
import gov.va.vro.abddataaccess.model.AbdProcedure;
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
import java.util.Objects;

/**
 * Unit tests for FhirClient.
 *
 * @author warren @Date 8/30/22
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
class FhirClientTest {

  private static final String TEST_PATIENT = "9000682";
  private static final String TEST_DIAGNOSTIC_CODE = "7101";
  private static final String TEST_MEDICATION_REQUEST = "6602";
  private static final String TEST_CLAIM_ID = "1234";

  private static final String CONTENT_TYPE = "application/fhir+json";
  private static final String MEDICATION_REQUEST_RESPONSE = "medication-response-bundle.json";
  private static final String OBSERVATION_RESPONSE = "observation-response-bundle.json";
  private static final String CONDITION_RESPONSE = "condition-response-bundle.json";
  private static final String PROCEDURE_RESPONSE = "procedure-response-bundle.json";

  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 30;

  @Spy private final FhirClient client = new FhirClient();

  private IParser parser;
  private Bundle medicationBundle;
  private Bundle bpBundle;

  @BeforeEach
  void setUp() {
    FhirContext fhirContext = FhirContext.forR4();
    EncodingEnum respType = EncodingEnum.forContentType(CONTENT_TYPE);
    parser = respType.newParser(fhirContext);

    String testfile =
        Objects.requireNonNull(getClass().getClassLoader().getResource(MEDICATION_REQUEST_RESPONSE))
            .getPath();
    medicationBundle = getMedicalInfoBundle(testfile);

    testfile =
        Objects.requireNonNull(getClass().getClassLoader().getResource(OBSERVATION_RESPONSE))
            .getPath();
    bpBundle = getMedicalInfoBundle(testfile);
  }

  private void mockGetBundle(Bundle bundle, AbdDomain domain) throws AbdException {
    Mockito.doReturn(bundle)
        .when(client)
        .getBundle(domain, TEST_PATIENT, DEFAULT_PAGE, DEFAULT_SIZE);
    if (bundle.hasEntry()) {
      Mockito.doReturn(new Bundle())
          .when(client)
          .getBundle(domain, TEST_PATIENT, DEFAULT_PAGE + 1, DEFAULT_SIZE);
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
      AbdEvidence evidence = client.getMedicalEvidence(testClaim);
      assertNotNull(evidence);
      assertTrue(evidence.getMedications().size() > 0);
      assertEquals(evidence.getMedications().size(), medicationBundle.getEntry().size());
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
      assertEquals(evidence.getBloodPressures().size(), bpBundle.getEntry().size());
    } catch (Exception e) {
      log.error("testGetBloodPressure error: {}", e.getMessage(), e);
      fail("testGetBloodPressure");
    }
  }

  @Test
  public void testGetAbdEvidence() {
    Map<AbdDomain, List<Bundle.BundleEntryComponent>> domainBundles = new HashMap<>();
    try {
      String testfile =
          Objects.requireNonNull(
                  getClass().getClassLoader().getResource(MEDICATION_REQUEST_RESPONSE))
              .getPath();
      Bundle bundle = getMedicalInfoBundle(testfile);
      assertNotNull(bundle);
      domainBundles.put(AbdDomain.MEDICATION, bundle.getEntry());

      testfile =
          Objects.requireNonNull(getClass().getClassLoader().getResource(OBSERVATION_RESPONSE))
              .getPath();
      bundle = getMedicalInfoBundle(testfile);
      assertNotNull(bundle);
      domainBundles.put(AbdDomain.BLOOD_PRESSURE, bundle.getEntry());

      testfile =
          Objects.requireNonNull(getClass().getClassLoader().getResource(CONDITION_RESPONSE))
              .getPath();
      bundle = getMedicalInfoBundle(testfile);
      assertNotNull(bundle);
      domainBundles.put(AbdDomain.CONDITION, bundle.getEntry());

      testfile =
          Objects.requireNonNull(getClass().getClassLoader().getResource(PROCEDURE_RESPONSE))
              .getPath();
      bundle = getMedicalInfoBundle(testfile);
      assertNotNull(bundle);
      domainBundles.put(AbdDomain.PROCEDURE, bundle.getEntry());

      AbdEvidence evidence = client.getAbdEvidence(domainBundles);
      assertNotNull(evidence);
      List<AbdMedication> medications = evidence.getMedications();
      assertNotNull(medications);
      List<AbdBloodPressure> bp = evidence.getBloodPressures();
      assertNotNull(bp);
      List<AbdCondition> conditions = evidence.getConditions();
      assertNotNull(conditions);
      List<AbdProcedure> procedures = evidence.getProcedures();
      assertNotNull(procedures);
    } catch (Exception e) {
      log.error("testGetAbdEvidence error: {}", e.getMessage(), e);
      fail("testGetAbdEvidence");
    }
  }

  private Bundle getMedicalInfoBundle(String filename) {
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
  void tearDown() {}
}
