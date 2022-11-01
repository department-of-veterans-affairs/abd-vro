package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
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

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasCollectionService {

  private final MasApiService masCollectionAnnotsApiService;

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

  public GeneratePdfPayload collectAnnotations(MasAutomatedClaimPayload claimPayload)
      throws MasException {
    var evidence = getCollectionAnnotations(claimPayload);
    return getGeneratePdfPayload(claimPayload, evidence);
  }

  private AbdEvidence getCollectionAnnotations(MasAutomatedClaimPayload claimPayload)
      throws MasException {

    log.info(
        "Collection {} is ready for processing, calling collection annotation service ",
        claimPayload.getCollectionId());
    AbdEvidence abdEvidence = null;
    try {
      var response =
          masCollectionAnnotsApiService.getCollectionAnnots(claimPayload.getCollectionId());
      for (MasCollectionAnnotation masCollectionAnnotation : response) {
        log.info(
            "Collection Annotation Response : Collection ID  {}",
            masCollectionAnnotation.getCollectionsId());
        log.info(
            "Collection Status Response : Veteran FileId  {}  ",
            masCollectionAnnotation.getVtrnFileId());

        MasCollectionAnnotsResults masCollectionAnnotsResults = new MasCollectionAnnotsResults();
        abdEvidence = masCollectionAnnotsResults.mapAnnotsToEvidence(masCollectionAnnotation);

        log.info("AbdEvidence : Medications {}  ", abdEvidence.getMedications().size());
        log.info("AbdEvidence : Conditions {}  ", abdEvidence.getConditions().size());
        log.info("AbdEvidence : BP {}  ", abdEvidence.getBloodPressures().size());
        break;
      }
    } catch (Exception e) {
      log.error("Error in calling collection Status API ", e);
      throw new MasException(e.getMessage(), e);
    }
    return abdEvidence;
  }

  private static GeneratePdfPayload getGeneratePdfPayload(
      MasAutomatedClaimPayload claimPayload, AbdEvidence abdEvidence) {
    GeneratePdfPayload generatePdfPayload = new GeneratePdfPayload();
    generatePdfPayload.setEvidence(abdEvidence);
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
