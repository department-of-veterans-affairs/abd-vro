package gov.va.vro.abd_data_access.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import gov.va.vro.abd_data_access.exception.AbdException;
import gov.va.vro.abd_data_access.model.*;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class FhirClient {
  private static final String LIGHTHOUSE_AUTH_HEAD = "Authorization";
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 30;

  @Autowired private IGenericClient client;

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
              "7101", new AbdDomain[] {AbdDomain.BLOOD_PRESSURE, AbdDomain.MEDICATION}),
          new AbstractMap.SimpleEntry<>("6602", new AbdDomain[] {AbdDomain.MEDICATION}));

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

  public Bundle getBundle(AbdDomain domain, String patientIcn, int pageNo, int pageSize)
      throws AbdException {
    SearchSpec searchSpec = domainToSearchSpec.get(domain).apply(patientIcn);
    String url = searchSpec.getUrl() + "&page=" + pageNo + "&count=" + pageSize;
    String lighthouseToken = lighthouseApiService.getLighthouseToken(domain, patientIcn);
    log.info("Get FHIR data from {}", url);
    return client
        .search()
        .byUrl(url)
        .returnBundle(Bundle.class)
        .withAdditionalHeader(LIGHTHOUSE_AUTH_HEAD, lighthouseToken)
        .execute();
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

  public List<AbdBloodPressure> getPatientBloodPressures(List<BundleEntryComponent> entries) {
    List<AbdBloodPressure> result = new ArrayList<>();
    for (BundleEntryComponent entry : entries) {
      Observation resource = (Observation) entry.getResource();
      AbdBloodPressure summary = FieldExtractor.extractBloodPressure(resource);
      result.add(summary);
    }
    if (result.size() < 1) {
      return null;
    }
    result.sort(null);
    return result;
  }

  public Map<AbdDomain, List<BundleEntryComponent>> getDomainBundles(AbdClaim claim)
      throws AbdException {
    AbdDomain[] domains = dpToDomains.get(claim.getDiagnosticCode());
    if (domains == null) {
      return null;
    }
    Map<AbdDomain, List<BundleEntryComponent>> result = new HashMap<>();
    String patientIcn = claim.getVeteranIcn();
    for (AbdDomain domain : domains) {
      int pageNo = DEFAULT_PAGE;
      boolean hasNextPage;
      List<BundleEntryComponent> records = new ArrayList<>();
      do {
        pageNo++;
        Bundle bundle = getBundle(domain, patientIcn, pageNo, DEFAULT_SIZE);
        List<BundleEntryComponent> entries = bundle.getEntry();
        if (entries.size() > 0) {
          records.addAll(entries);
        }
        if (bundle.hasLink()) {
          hasNextPage = bundle.getLink().stream().anyMatch(l -> l.getRelation().equals("next"));
        } else {
          hasNextPage = false;
        }
      } while (hasNextPage);
      if (!records.isEmpty()) {
        result.put(domain, records);
      }
    }
    return result;
  }

  public AbdEvidence getMedicalEvidence(AbdClaim claim) throws AbdException {
    Map<AbdDomain, List<BundleEntryComponent>> components = getDomainBundles(claim);
    if (components == null) {
      return null;
    }
    return getAbdEvidence(components);
  }

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
          if (bps != null) {
            result.setBloodPressures(bps);
          }
        }
      }
    }
    return result;
  }
}
