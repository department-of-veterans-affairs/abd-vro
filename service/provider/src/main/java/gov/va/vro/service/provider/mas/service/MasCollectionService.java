package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.*;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasCollectionStatus;
import gov.va.vro.model.mas.MasStatus;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.mapper.MasCollectionAnnotsResults;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasCollectionService {

  private final IMasApiService masCollectionAnnotsApiService;

  public boolean checkCollectionStatus(int collectionId) throws MasException {

    log.info("Checking collection status for collection {}.", collectionId);
    try {
      var response =
          masCollectionAnnotsApiService.getMasCollectionStatus(
              Collections.singletonList(collectionId));
      log.info("Collection Status Response : response Size: " + response.size());
      for (MasCollectionStatus masCollectionStatus : response) {
        log.info(
            "Collection Status Response : Collection ID  {} ",
            masCollectionStatus.getCollectionsId());
        log.info(
            "Collection Status Response : Collection Status {} ",
            masCollectionStatus.getCollectionStatus());
        if ((MasStatus.PROCESSED)
            .equals(MasStatus.getMasStatus(masCollectionStatus.getCollectionStatus()))) {
          return true;
        }
      }
    } catch (Exception e) {
      log.error("Error in calling collection Status API ", e);
      throw new MasException("Error in calling collection Status API ", e);
    }
    return false;
  }

  public HealthDataAssessment collectAnnotations(MasAutomatedClaimPayload claimPayload)
      throws MasException {

    log.info(
        "Collection {} is ready for processing, calling collection annotation service ",
        claimPayload.getCollectionId());

    var response =
        masCollectionAnnotsApiService.getCollectionAnnots(claimPayload.getCollectionId());
    if (response.isEmpty()) {
      throw new MasException(
          "No annotations found for collection id " + claimPayload.getCollectionId());
    }
    MasCollectionAnnotation masCollectionAnnotation = response.get(0);
    log.info(
        "Collection Annotation Response : Collection ID  {} and Veteran File ID {}",
        masCollectionAnnotation.getCollectionsId(),
        masCollectionAnnotation.getVtrnFileId());

    MasCollectionAnnotsResults masCollectionAnnotsResults = new MasCollectionAnnotsResults();
    AbdEvidence abdEvidence =
        masCollectionAnnotsResults.mapAnnotsToEvidence(masCollectionAnnotation);

    log.info("AbdEvidence : Medications {}  ", abdEvidence.getMedications().size());
    log.info("AbdEvidence : Conditions {}  ", abdEvidence.getConditions().size());
    log.info("AbdEvidence : BP {}  ", abdEvidence.getBloodPressures().size());

    HealthDataAssessment healthDataAssessment = new HealthDataAssessment();
    healthDataAssessment.setDiagnosticCode(claimPayload.getDiagnosticCode());
    healthDataAssessment.setEvidence(abdEvidence);
    healthDataAssessment.setVeteranIcn(claimPayload.getVeteranIdentifiers().getIcn());
    healthDataAssessment.setDisabilityActionType(
        claimPayload.getClaimDetail().getConditions().getDisabilityActionType());
    return healthDataAssessment;
  }

  public static HealthDataAssessment combineEvidence(
      HealthDataAssessment lighthouseAssessment, HealthDataAssessment masApiAssessment) {
    AbdEvidence lighthouseEvidence = lighthouseAssessment.getEvidence();
    AbdEvidence masApiEvidence = masApiAssessment.getEvidence();
    // for now, we just add up the lists
    log.info("combineEvidence >> LH  : " + lighthouseEvidence != null ? "not null" : "null");
    log.info("combineEvidence >> MAS : " + masApiEvidence != null ? "not null" : "null");
    AbdEvidence compositeEvidence = new AbdEvidence();
    compositeEvidence.setBloodPressures(
        merge(
                lighthouseEvidence != null ? lighthouseEvidence.getBloodPressures() : null,
                masApiEvidence != null ? masApiEvidence.getBloodPressures() : null)
            .stream()
            .flatMap(s -> Stream.ofNullable(s))
            .distinct()
            .collect(Collectors.toList()));
    compositeEvidence.setConditions(
        merge(
                lighthouseEvidence != null ? lighthouseEvidence.getConditions() : null,
                masApiEvidence != null ? masApiEvidence.getConditions() : null)
            .stream()
            .flatMap(s -> Stream.ofNullable(s))
            .distinct()
            .collect(Collectors.toList()));
    compositeEvidence.setMedications(
        merge(
                lighthouseEvidence != null ? lighthouseEvidence.getMedications() : null,
                masApiEvidence != null ? masApiEvidence.getMedications() : null)
            .stream()
            .flatMap(s -> Stream.ofNullable(s))
            .distinct()
            .collect(Collectors.toList()));
    compositeEvidence.setProcedures(
        merge(
                lighthouseEvidence != null ? lighthouseEvidence.getProcedures() : null,
                masApiEvidence != null ? masApiEvidence.getProcedures() : null)
            .stream()
            .flatMap(s -> Stream.ofNullable(s))
            .distinct()
            .collect(Collectors.toList()));
    lighthouseAssessment.setEvidence(compositeEvidence);

    return lighthouseAssessment;
  }

  private static <T> List<T> merge(List<T> list1, List<T> list2) {
    List<T> result = new ArrayList<>();
    if (list1 != null) {
      result.addAll(list1);
    }
    if (list2 != null) {
      result.addAll(list2);
    }
    return result;
  }

  public static GeneratePdfPayload getGeneratePdfPayload(MasTransferObject transferObject) {
    MasAutomatedClaimPayload claimPayload = transferObject.getClaimPayload();
    GeneratePdfPayload generatePdfPayload = new GeneratePdfPayload();
    generatePdfPayload.setEvidence(transferObject.getEvidence());
    generatePdfPayload.setClaimSubmissionId(claimPayload.getClaimDetail().getBenefitClaimId());
    generatePdfPayload.setDiagnosticCode(
        claimPayload.getClaimDetail().getConditions().getDiagnosticCode());
    VeteranInfo veteranInfo = new VeteranInfo();
    veteranInfo.setFirst(claimPayload.getFirstName());
    veteranInfo.setLast(claimPayload.getLastName());
    veteranInfo.setMiddle("");
    veteranInfo.setBirthdate(claimPayload.getDateOfBirth());
    generatePdfPayload.setVeteranInfo(veteranInfo);
    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        generatePdfPayload.getClaimSubmissionId(),
        generatePdfPayload.getDiagnosticCode());
    return generatePdfPayload;
  }
}
