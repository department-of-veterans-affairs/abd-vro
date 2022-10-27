package gov.va.vro.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.GeneratePdfResp;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasCollectionStatus;
import gov.va.vro.model.mas.MasStatus;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasApiService;
import gov.va.vro.service.provider.mas.service.mapper.MasCollectionAnnotsResults;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {
  private final CamelEntrance camelEntrance;
  private final MasDelays masDelays;
  private final MasApiService masCollectionAnnotsApiService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    // TODO : Remove the extraneous log statements
    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);

    boolean isCollectionReady = checkCollectionStatus(claimPayload);

    if (isCollectionReady) {
      AbdEvidence abdEvidence = getCollectionAnnots(claimPayload);
      // call Lighthouse
      // Combine results and call PDF generation
      GeneratePdfResp generatePdfResp = generatePdf(claimPayload, abdEvidence);
      // call pcOrderExam in the absence of evidence
    } else {
      log.info("Collection {} is not ready. Requeue..ing...", claimPayload.getCollectionId());
      // re-request after some time
      camelEntrance.notifyAutomatedClaim(claimPayload, masDelays.getMasProcessingSubsequentDelay());
    }
  }

  public boolean checkCollectionStatus(MasAutomatedClaimPayload claimPayload) throws MasException {

    log.info("Checking collection status for collection {}.", claimPayload.getCollectionId());
    boolean isCollectionReady = false;
    try {
      List<Integer> collectionIds = new ArrayList<Integer>();
      collectionIds.add(claimPayload.getCollectionId());
      var response = masCollectionAnnotsApiService.getMasCollectionStatus(collectionIds);
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
          isCollectionReady = true;
        }
      }
    } catch (Exception e) {
      log.error("Error in calling collection Status API ", e);
      throw new MasException(e.getMessage(), e);
    }
    return isCollectionReady;
  }

  public AbdEvidence getCollectionAnnots(MasAutomatedClaimPayload claimPayload)
      throws MasException {

    log.info(
        "Collection {} is ready for processing, calling collection annotation service ",
        claimPayload.getCollectionId());
    AbdEvidence abdEvidence = new AbdEvidence();
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

  public GeneratePdfResp generatePdf(MasAutomatedClaimPayload claimPayload, AbdEvidence abdEvidence)
      throws MasException {

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
    try {
      log.info(generatePdfPayload.toString());
      String response = camelEntrance.generatePdf(generatePdfPayload);
      log.info(response.toString());
      GeneratePdfResp pdfResponse = objectMapper.readValue(response, GeneratePdfResp.class);
      log.info(pdfResponse.toString());
      log.info("RESPONSE from generatePdf returned status: {}", pdfResponse.getStatus());
      if (pdfResponse.getStatus().equals("ERROR")) {
        log.info("RESPONSE from generatePdf returned error reason: {}", pdfResponse.getReason());
        throw new Exception(
            "RESPONSE from generatePdf returned error reason: " + pdfResponse.getReason());
      } else {
        return pdfResponse;
      }
    } catch (Exception e) {
      log.error("Error in generate pdf", e);
      throw new MasException(e.getMessage(), e);
    }
  }
}
