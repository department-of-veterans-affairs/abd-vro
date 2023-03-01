package gov.va.vro.abddataaccess.service;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.exception.AbdException;
import gov.va.vro.abddataaccess.model.AbdClaim;
import gov.va.vro.model.AbdBloodPressure;
import gov.va.vro.model.AbdCondition;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.AbdMedication;
import gov.va.vro.model.AbdProcedure;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class FhirClient {
  private static final String LIGHTHOUSE_AUTH_HEAD = "Authorization";
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 100;

  @Autowired private LighthouseProperties properties;

  @Autowired private RestTemplate restTemplate;

  @Autowired private IParser jsonParser;

  @Autowired private LighthouseApiService lighthouseApiService;

  private static class SearchSpec {
    private String resourceType;
    private String[] searchParams;
    private String[] searchValues;

    public SearchSpec(String resourceType) {
      this.resourceType = resourceType;
    }

    public SearchSpec(String resourceType, String id) {
      this.resourceType = resourceType;
      searchParams = new String[] {"patient"};
      searchValues = new String[] {id};
    }

    public void setSearchParams(String[] searchParams) {
      this.searchParams = searchParams;
    }

    public void setSearchValues(String[] searchValues) {
      this.searchValues = searchValues;
    }

    public String getUrl() {
      StringBuilder url =
          new StringBuilder(
              String.format("%s?%s=%s", resourceType, searchParams[0], searchValues[0]));
      for (int i = 1; i < searchParams.length; ++i) {
        url.append(String.format("&%s=%s", searchParams[i], searchValues[i]));
      }
      return url.toString();
    }
  }

  private static final Map<String, AbdDomain[]> dpToDomains =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<>(
              "0",
              new AbdDomain[] {
                AbdDomain.BLOOD_PRESSURE,
                AbdDomain.CONDITION,
                AbdDomain.PROCEDURE,
                AbdDomain.MEDICATION
              }),
          new AbstractMap.SimpleEntry<>(
              "7101",
              new AbdDomain[] {
                AbdDomain.BLOOD_PRESSURE, AbdDomain.MEDICATION, AbdDomain.CONDITION
              }),
          new AbstractMap.SimpleEntry<>("6602", new AbdDomain[] {AbdDomain.MEDICATION}),
          new AbstractMap.SimpleEntry<>(
              "6522",
              new AbdDomain[] {AbdDomain.MEDICATION, AbdDomain.CONDITION, AbdDomain.PROCEDURE}),
          new AbstractMap.SimpleEntry<>(
              "6602v2", new AbdDomain[] {AbdDomain.MEDICATION, AbdDomain.CONDITION}),
          new AbstractMap.SimpleEntry<>(
              "6510",
              new AbdDomain[] {AbdDomain.MEDICATION, AbdDomain.PROCEDURE, AbdDomain.CONDITION}),
          new AbstractMap.SimpleEntry<>(
              "cancer", new AbdDomain[] {AbdDomain.MEDICATION, AbdDomain.CONDITION}));

  private static final Map<AbdDomain, Function<String, SearchSpec>> domainToSearchSpec =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<AbdDomain, Function<String, SearchSpec>>(
              AbdDomain.BLOOD_PRESSURE,
              (id) -> {
                SearchSpec result = new SearchSpec("Observation");
                result.setSearchParams(new String[] {"patient", "code"});
                result.setSearchValues(new String[] {id, "85354-9"});
                return result;
              }),
          new AbstractMap.SimpleEntry<AbdDomain, Function<String, SearchSpec>>(
              AbdDomain.MEDICATION, (id) -> new SearchSpec("MedicationRequest", id)),
          new AbstractMap.SimpleEntry<AbdDomain, Function<String, SearchSpec>>(
              AbdDomain.PROCEDURE, (id) -> new SearchSpec("Procedure", id)),
          new AbstractMap.SimpleEntry<AbdDomain, Function<String, SearchSpec>>(
              AbdDomain.CONDITION, (id) -> new SearchSpec("Condition", id)));

  /**
   * Gets a FHIR {@link Bundle} for the given parameters.
   *
   * @param url a URL string.
   * @param lighthouseToken a token to access Lighthouse FHIR API..
   * @return a {@link Bundle}.
   * @throws AbdException when error occurs.
   */
  public Bundle getFhirBundle(String url, String lighthouseToken) throws AbdException {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(LIGHTHOUSE_AUTH_HEAD, lighthouseToken);
    HttpEntity request = new HttpEntity(headers);

    log.info("Get FHIR data from {}", url);
    try {
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, request, String.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        log.error("Unexpected response from lighthouse {}", response.getStatusCode());
        log.error("Body is {}", response.getBody());
        throw new AbdException("Unable to get the bundle for the patient.");
      }
      String strBody = response.getBody();
      Bundle bundle = jsonParser.parseResource(Bundle.class, strBody);
      return bundle;
    } catch (RestClientException ex) {
      log.error("Unable to get bundle from {}", url, ex);
      throw new AbdException("Unable to get the bundle for the patient.");
    } catch (DataFormatException dfEx) {
      log.error("Unable to parse the bundle from {}", url, dfEx);
      throw new AbdException("Unable to parse bundle for the patient.");
    }
  }

  private List<AbdCondition> getPatientConditions(List<BundleEntryComponent> entries) {
    List<AbdCondition> result = new ArrayList<>();
    for (BundleEntryComponent entry : entries) {
      Condition resource = (Condition) entry.getResource();
      AbdCondition summary = FieldExtractor.extractCondition(resource);
      result.add(summary);
    }
    result.sort(null);
    return result;
  }

  private List<AbdMedication> getPatientMedications(List<BundleEntryComponent> entries) {
    log.info("Extract patient medication entries. number of entries: {}", entries.size());
    List<AbdMedication> result = new ArrayList<>();
    for (BundleEntryComponent entry : entries) {
      MedicationRequest resource = (MedicationRequest) entry.getResource();
      AbdMedication summary = FieldExtractor.extractMedication(resource);
      result.add(summary);
    }
    result.sort(null);
    return result;
  }

  private List<AbdProcedure> getPatientProcedures(List<BundleEntryComponent> entries) {
    List<AbdProcedure> result = new ArrayList<>();
    for (BundleEntryComponent entry : entries) {
      Procedure resource = (Procedure) entry.getResource();
      AbdProcedure summary = FieldExtractor.extractProcedure(resource);
      result.add(summary);
    }
    result.sort(null);
    return result;
  }

  /**
   * Gets a list of blood pressure readings.
   *
   * @param entries a list of entries from a FHIR bundle.
   * @return a list of {@link AbdBloodPressure}.
   */
  public List<AbdBloodPressure> getPatientBloodPressures(List<BundleEntryComponent> entries) {
    log.info("Extract patient blood pressure entries. number of entries: {}", entries.size());
    List<AbdBloodPressure> result = new ArrayList<>();
    for (BundleEntryComponent entry : entries) {
      Observation resource = (Observation) entry.getResource();
      AbdBloodPressure summary = FieldExtractor.extractBloodPressure(resource);
      result.add(summary);
    }
    result.sort(null);
    return result;
  }

  /**
   * Gets domains for the given claim.
   *
   * @param claim an {@link AbdClaim}.
   * @return a map of domains associated with the claim.
   * @throws AbdException error occurs.
   */
  public Map<AbdDomain, List<BundleEntryComponent>> getDomainBundles(AbdClaim claim)
      throws AbdException {
    AbdDomain[] domains = dpToDomains.get(claim.getDiagnosticCode());
    if (domains == null) {
      log.error("domains not found for the claim diagnostic code.");
      return null;
    }

    Map<AbdDomain, List<BundleEntryComponent>> result = new HashMap<>();
    String patientIcn = claim.getVeteranIcn();
    for (AbdDomain domain : domains) {
      String lighthouseToken = lighthouseApiService.getLighthouseToken(domain, patientIcn);
      List<BundleEntryComponent> records = getRecords(patientIcn, domain, lighthouseToken);
      result.put(domain, records);
    }
    return result;
  }

  private List<BundleEntryComponent> getRecords(
      String patientIcn, AbdDomain domain, String lighthouseToken) throws AbdException {
    SearchSpec searchSpec = domainToSearchSpec.get(domain).apply(patientIcn);
    String url = searchSpec.getUrl() + "&_count=" + DEFAULT_SIZE;

    String baseUrl = properties.getFhirurl();
    String fullUrl = baseUrl + "/" + url;
    log.info("Retrieve data for {} for ICN = {}", domain.name(), patientIcn);

    List<BundleEntryComponent> records = new ArrayList<>();
    String nextLink = fullUrl;
    do {
      log.info("Next link is {}", nextLink);
      Bundle bundle = getFhirBundle(nextLink, lighthouseToken);
      nextLink = "";
      if (bundle.hasLink()) {
        Optional<Bundle.BundleLinkComponent> next =
            bundle.getLink().stream().filter(l -> l.getRelation().equals("next")).findFirst();
        if (next.isPresent()) {
          nextLink = next.get().getUrl();
        }
      }
      List<BundleEntryComponent> entries = bundle.getEntry();
      if (entries.size() > 0) {
        log.info("Adding {} entries to records", entries.size());
        records.addAll(entries);
      } else {
        log.info("Empty page from fhir service. Ending pulling resources.");
        break;
      }
    } while (!nextLink.isEmpty());
    return records;
  }

  /**
   * Gets medical evidence for a claim.
   *
   * @param claim a claim.
   * @return a medical evidence object.
   * @throws AbdException error occurs.
   */
  public AbdEvidence getMedicalEvidence(AbdClaim claim) throws AbdException {
    Map<AbdDomain, List<BundleEntryComponent>> components = getDomainBundles(claim);
    if (components == null) {
      return null;
    }
    return getAbdEvidence(components);
  }

  /**
   * Gets an AbdEvidence object from a set of domain components.
   *
   * @param components a map of AbdDomain components.
   * @return an {@link AbdEvidence}.
   */
  @NotNull
  public AbdEvidence getAbdEvidence(Map<AbdDomain, List<BundleEntryComponent>> components) {
    AbdEvidence result = new AbdEvidence();
    for (Map.Entry<AbdDomain, List<BundleEntryComponent>> entryComponent : components.entrySet()) {
      List<BundleEntryComponent> entries = entryComponent.getValue();
      switch (entryComponent.getKey()) {
        case MEDICATION -> {
          List<AbdMedication> medications = getPatientMedications(entries);
          result.setMedications(medications);
        }
        case CONDITION -> {
          List<AbdCondition> conditions = getPatientConditions(entries);
          result.setConditions(conditions);
        }
        case PROCEDURE -> {
          List<AbdProcedure> procedures = getPatientProcedures(entries);
          result.setProcedures(procedures);
        }
        case BLOOD_PRESSURE -> {
          List<AbdBloodPressure> bps = getPatientBloodPressures(entries);
          result.setBloodPressures(bps);
        }
        default -> {
        }
      }
    }
    return result;
  }
}
