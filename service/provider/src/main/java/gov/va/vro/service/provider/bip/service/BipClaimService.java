package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bip.UpdateContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.bipevidence.BipFileProviderData;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.model.bipevidence.response.UploadResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.ClaimProps;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.services.DiagnosisLookup;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BipClaimService {

  public static final String TSOJ = "398";

  private final ClaimProps claimPorps;

  private final IBipApiService bipApiService;

  private final IBipCeApiService bipCeApiService;

  private final SaveToDbService saveToDbService;

  /**
   * Check if all the anchors for fast-tracking are satisfied.
   *
   * @param claimId claim id identifying the claim
   * @return true if the anchors are satisfied, false otherwise
   */
  public boolean hasAnchors(long claimId) {

    var claimDetails = bipApiService.getClaimDetails(claimId);
    if (claimDetails == null) {
      log.warn("Claim with claim Id {} not found in BIP", claimId);
      return false;
    }
    if (!TSOJ.equals(claimDetails.getTempStationOfJurisdiction())) {
      log.info("Claim with claim Id {} does not have TSOJ = {}", claimId, TSOJ);
      return false;
    }

    var contentions = bipApiService.getClaimContentions(claimId);
    if (contentions == null) {
      log.info("Claim with claim Id {} does not have contentions.", claimId);
      return false;
    }
    log.info(
        "SPECIAL_ISSUE_1: {}, SPECIAL_ISSUE_2: {}",
        claimPorps.getSpecialIssue1(),
        claimPorps.getSpecialIssue2());
    // collect all special issues
    var specialIssues =
        contentions.stream()
            .filter(BipClaimService::hasSpecialIssues)
            .map(ClaimContention::getSpecialIssueCodes)
            .flatMap(Collection::stream)
            .map(String::toLowerCase) // Ignore case
            .collect(Collectors.toSet());
    boolean hasSpecialIssues =
        specialIssues.contains(claimPorps.getSpecialIssue1().toLowerCase())
            && specialIssues.contains(claimPorps.getSpecialIssue2().toLowerCase());
    log.info("Has special issues: {}", hasSpecialIssues);
    return hasSpecialIssues;
  }

  /**
   * Remove special issue contention.
   *
   * @param payload the claim payload
   * @return the claim payload
   */
  public MasProcessingObject removeSpecialIssue(MasProcessingObject payload) {
    var claimId = Long.parseLong(payload.getBenefitClaimId());
    String specialIssue1 = claimPorps.getSpecialIssue1();
    log.info("Attempting to remove special issue {} for claim id = {}", specialIssue1, claimId);

    var contentions = bipApiService.getClaimContentions(claimId);
    if (ObjectUtils.isEmpty(contentions)) {
      log.warn("Claim id = {} has no contentions.", claimId);
      return payload;
    }

    List<ClaimContention> updatedContentions = new ArrayList<>();
    for (ClaimContention contention : contentions) {
      List<String> specialIssueCodes = contention.getSpecialIssueCodes();
      if (specialIssueCodes == null) {
        log.info("Contention {} has no special issues.", contention.getContentionId());
        continue;
      }
      log.info("Special issue codes: {}", String.join(",", specialIssueCodes));
      var codes = specialIssueCodes.stream().map(String::toLowerCase).collect(Collectors.toSet());
      if (codes.contains(specialIssue1.toLowerCase())) {
        log.info("Found {} in contention {}", specialIssue1, contention.getContentionId());
        // remove string from contention
        List<String> updatedCodes =
            contention.getSpecialIssueCodes().stream()
                .filter(code -> !claimPorps.getSpecialIssue1().equalsIgnoreCase(code))
                .collect(Collectors.toList());
        var update = contention.toBuilder().specialIssueCodes(updatedCodes).build();
        updatedContentions.add(update);
      }
    }
    if (updatedContentions.isEmpty()) {
      log.info("Special issue for claim id = {} not found. Nothing to update.", claimId);
      return payload; // nothing to update
    }
    log.info("Removing special issue for claim id = {}", claimId);
    String action = "UPDATED_CONTENTION";
    List<UpdateContention> updateContentions =
        updatedContentions.stream()
            .map(c -> c.toUpdateContention(action))
            .collect(Collectors.toList());
    UpdateContentionReq request =
        UpdateContentionReq.builder().updateContentions(updateContentions).build();
    bipApiService.updateClaimContention(claimId, request);
    return payload;
  }

  /**
   * Update claim status.
   *
   * @param payload the claim payload
   * @return the claim payload
   */
  public MasProcessingObject markAsRfd(MasProcessingObject payload) {
    long claimId = payload.getBenefitClaimIdAsLong();
    int collectionId = payload.getCollectionId();

    // check again if TSOJ. If not, abandon route
    var claim = bipApiService.getClaimDetails(claimId);
    if (!TSOJ.equals(claim.getTempStationOfJurisdiction())) {
      log.info(
          "Claim {} with collection Id = {} is in state {}. Not updating status",
          claimId,
          collectionId,
          claim.getTempStationOfJurisdiction());
    } else {
      log.info("Marking claim with claimId = {} as Ready For Decision", claimId);
      try {
        bipApiService.updateClaimStatus(claimId, ClaimStatus.RFD);
        saveToDbService.updateRfdFlag(String.valueOf(claimId), true);
      } catch (Exception e) {
        throw new BipException("BIP update claim status resulted in an exception", e);
      }
    }
    return payload;
  }

  /** Check if claim is still eligible for fast tracking, and if so, update status. */
  public MasProcessingObject completeProcessing(MasProcessingObject payload) {
    int collectionId = payload.getCollectionId();
    long claimId = payload.getBenefitClaimIdAsLong();

    // check again if TSOJ. If not, abandon route
    var claim = bipApiService.getClaimDetails(claimId);
    if (!TSOJ.equals(claim.getTempStationOfJurisdiction())) {
      log.info(
          "Claim {} with collection Id = {} is in state {}. Status not updated",
          claimId,
          collectionId,
          claim.getTempStationOfJurisdiction());
      payload.setTSOJ(false);
      return payload;
    }
    payload.setTSOJ(true);
    return payload;
  }

  /**
   * Uploads a pdf.
   *
   * @param pdfResponse pdf response.
   * @return pdf response.
   * @throws BipException if anything goes wrong
   */
  public FetchPdfResponse uploadPdf(MasAutomatedClaimPayload payload, FetchPdfResponse pdfResponse)
      throws BipException {
    log.info("Uploading pdf for claim {}...", pdfResponse.getClaimSubmissionId());
    if (pdfResponse.getPdfData() == null) {
      throw new BipException("PDF Response does not contain any data");
    }
    String filename =
        GeneratePdfPayload.createPdfFilename(
            DiagnosisLookup.getDiagnosis(payload.getDiagnosticCode()));

    byte[] decoder = Base64.getDecoder().decode(pdfResponse.getPdfData());
    BipFileProviderData providerData =
        BipFileProviderData.builder()
            .contentSource("VRO")
            .claimantFirstName(payload.getFirstName())
            .claimantLastName(payload.getLastName())
            .claimantSsn(payload.getVeteranIdentifiers().getSsn())
            .documentTypeId(1489)
            .dateVaReceivedDocument(LocalDate.now().toString())
            .subject(pdfResponse.getDiagnosis()) // get a subject
            .notes(pdfResponse.getReason() == null ? null : pdfResponse.getReason())
            .claimantParticipantId(payload.getVeteranIdentifiers().getParticipantId())
            .sourceComment("upload from VRO")
            .claimantDateOfBirth(payload.getDateOfBirth())
            .build();

    BipFileUploadResp bipResp =
        bipCeApiService.uploadEvidenceFile(
            FileIdType.FILENUMBER,
            payload.getVeteranIdentifiers().getVeteranFileId(),
            BipFileUploadPayload.builder().contentName(filename).providerData(providerData).build(),
            decoder,
            payload.getDiagnosticCode());
    if (bipResp != null) {
      UploadResponse ur = bipResp.getUploadResponse();
      UUID eFolderId = UUID.fromString(ur.getUuid());
      saveToDbService.updateEvidenceSummaryDocument(eFolderId, payload);
    }
    return pdfResponse;
  }

  private static boolean hasSpecialIssues(ClaimContention claimContention) {
    return claimContention.getSpecialIssueCodes() != null;
  }
}
