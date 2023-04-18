package gov.va.vro.abddataaccess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.EncodingEnum;
import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.exception.AbdException;
import gov.va.vro.abddataaccess.model.AbdClaim;
import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
@ActiveProfiles("test")
class FhirClientTest {
  @AllArgsConstructor
  @Getter
  private static class BundleInfo {
    private Bundle bundle;
    private String responseBody;

    private static Bundle getMedicalInfoBundle(String filename) throws IOException {
      File initialFile = new File(filename);
      InputStream theResponseInputStream = new FileInputStream(initialFile);
      Bundle retVal = parser.parseResource(Bundle.class, theResponseInputStream);
      theResponseInputStream.close();
      return retVal;
    }

    public static BundleInfo getInstance(String resourceName) throws IOException {
      Class<?> clazz = FhirClientTest.class;
      URL url = clazz.getClassLoader().getResource(resourceName);

      String file = Objects.requireNonNull(url).getPath();
      Bundle bundle = getMedicalInfoBundle(file);
      String responseBody = new String(Files.readAllBytes(Paths.get(file)));
      return new BundleInfo(bundle, responseBody);
    }
  }

  private static final String TEST_PATIENT = "9000682";
  private static final String TEST_DIAGNOSTIC_CODE = "7101";
  private static final String TEST_MEDICATION_REQUEST = "6602";
  private static final String TEST_CLAIM_ID = "1234";

  private static final String CONTENT_TYPE = "application/fhir+json";
  private static final String MEDICATION_REQUEST_RESPONSE = "medication-response-bundle.json";
  private static final String OBSERVATION_RESPONSE = "observation-response-bundle.json";
  private static final String CONDITION_RESPONSE = "condition-response-bundle.json";
  private static final String PROCEDURE_RESPONSE = "procedure-response-bundle.json";
  private static final String EMPTY_RESPONSE = "empty-bundle.json";

  private static final String FHIR_URL = "https://sandbox-api.va.gov/";

  @InjectMocks private FhirClient client = Mockito.spy(new FhirClient());

  @Mock private LighthouseApiService lighthouseApiService;

  @Mock private RestTemplate restTemplate;

  @Mock private LighthouseProperties properties;

  @Mock private IParser jsonParser;

  private static IParser parser;

  private static BundleInfo medBundleInfo;
  private static BundleInfo bpBundleInfo;
  private static BundleInfo conditionBundleInfo;
  private static BundleInfo procedureBundleInfo;
  private static BundleInfo emptyBundleInfo;

  @BeforeAll
  private static void initVariables() throws IOException {
    FhirContext fhirContext = FhirContext.forR4();
    EncodingEnum respType = EncodingEnum.forContentType(CONTENT_TYPE);
    parser = respType.newParser(fhirContext);

    bpBundleInfo = BundleInfo.getInstance(OBSERVATION_RESPONSE);
    medBundleInfo = BundleInfo.getInstance(MEDICATION_REQUEST_RESPONSE);
    conditionBundleInfo = BundleInfo.getInstance(CONDITION_RESPONSE);
    procedureBundleInfo = BundleInfo.getInstance(PROCEDURE_RESPONSE);
    emptyBundleInfo = BundleInfo.getInstance(EMPTY_RESPONSE);
  }

  private void mockRest(ResponseEntity<String> resp, String domainName) {
    Mockito.doReturn(resp)
        .when(restTemplate)
        .exchange(
            ArgumentMatchers.contains(domainName),
            ArgumentMatchers.eq(HttpMethod.GET),
            ArgumentMatchers.any(HttpEntity.class),
            ArgumentMatchers.eq(String.class));
  }

  @Test
  public void testGetMedicalEvidence() throws AbdException {
    AbdClaim testClaim = new AbdClaim();
    testClaim.setClaimSubmissionId(TEST_CLAIM_ID);
    testClaim.setDiagnosticCode(TEST_MEDICATION_REQUEST);
    testClaim.setVeteranIcn(TEST_PATIENT);

    Mockito.doReturn(FHIR_URL).when(properties).getFhirurl();
    Mockito.doReturn(medBundleInfo.getBundle())
        .when(jsonParser)
        .parseResource(Bundle.class, medBundleInfo.getResponseBody());
    ResponseEntity<String> medicationResp = ResponseEntity.ok(medBundleInfo.getResponseBody());
    mockRest(medicationResp, "MedicationRequest");
    AbdEvidence evidence = client.getMedicalEvidence(testClaim);
    assertNotNull(evidence);
    assertTrue(evidence.getMedications().size() > 0);
    assertEquals(evidence.getMedications().size(), medBundleInfo.getBundle().getEntry().size());
  }

  @Test
  public void testGetBloodPressure() throws AbdException {
    AbdClaim testClaim = new AbdClaim();
    testClaim.setClaimSubmissionId(TEST_CLAIM_ID);
    testClaim.setDiagnosticCode(TEST_DIAGNOSTIC_CODE);
    testClaim.setVeteranIcn(TEST_PATIENT);

    Mockito.doReturn(FHIR_URL).when(properties).getFhirurl();
    Mockito.doReturn(medBundleInfo.getBundle())
        .when(jsonParser)
        .parseResource(Bundle.class, medBundleInfo.getResponseBody());
    Mockito.doReturn(bpBundleInfo.getBundle())
        .when(jsonParser)
        .parseResource(Bundle.class, bpBundleInfo.getResponseBody());
    Mockito.doReturn(conditionBundleInfo.getBundle())
        .when(jsonParser)
        .parseResource(Bundle.class, conditionBundleInfo.getResponseBody());
    ResponseEntity<String> medicationResp = ResponseEntity.ok(medBundleInfo.getResponseBody());
    ResponseEntity<String> bpResp = ResponseEntity.ok(bpBundleInfo.getResponseBody());
    ResponseEntity<String> conditionResp = ResponseEntity.ok(conditionBundleInfo.getResponseBody());
    mockRest(medicationResp, "MedicationRequest");
    mockRest(bpResp, "Observation");
    mockRest(conditionResp, "Condition");
    AbdEvidence evidence = client.getMedicalEvidence(testClaim);
    assertNotNull(evidence);

    assertTrue(evidence.getBloodPressures().size() > 0);
    assertEquals(evidence.getBloodPressures().size(), bpBundleInfo.getBundle().getEntry().size());
  }

  @Test
  public void testGetAbdEvidence() {
    Map<AbdDomain, List<Bundle.BundleEntryComponent>> domainBundles = new HashMap<>();

    Bundle medBundle = medBundleInfo.getBundle();
    assertNotNull(medBundle);
    domainBundles.put(AbdDomain.MEDICATION, medBundle.getEntry());

    Bundle bpBundle = bpBundleInfo.getBundle();
    assertNotNull(bpBundle);
    domainBundles.put(AbdDomain.BLOOD_PRESSURE, bpBundle.getEntry());

    Bundle conditionBundle = conditionBundleInfo.getBundle();
    assertNotNull(conditionBundle);
    domainBundles.put(AbdDomain.CONDITION, conditionBundle.getEntry());

    Bundle procedureBundle = procedureBundleInfo.getBundle();
    assertNotNull(procedureBundle);
    domainBundles.put(AbdDomain.PROCEDURE, procedureBundle.getEntry());

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
  }

  private static <T> void checkNotNullButEmpty(List<T> list) {
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  @Test
  public void testMedicalEvidenceEmptyBundles() throws AbdException {
    Mockito.doReturn("token")
        .when(lighthouseApiService)
        .getLighthouseToken(Mockito.any(), Mockito.any());

    Mockito.doReturn(emptyBundleInfo.getBundle())
        .when(client)
        .getFhirBundle(Mockito.any(), Mockito.any());

    AbdClaim claim = new AbdClaim("1234", "0", "34");
    AbdEvidence evidence = client.getMedicalEvidence(claim);

    checkNotNullButEmpty(evidence.getBloodPressures());
    checkNotNullButEmpty(evidence.getMedications());
    checkNotNullButEmpty(evidence.getConditions());
    checkNotNullButEmpty(evidence.getProcedures());
  }
}
