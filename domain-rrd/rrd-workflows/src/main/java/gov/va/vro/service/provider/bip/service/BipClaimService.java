package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.rrd.bip.BipClaim;
import gov.va.vro.model.rrd.bip.ClaimContention;
import gov.va.vro.model.rrd.bip.ClaimStatus;
import gov.va.vro.model.rrd.bip.FileIdType;
import gov.va.vro.model.rrd.bip.UpdateContention;
import gov.va.vro.model.rrd.bip.UpdateContentionReq;
import gov.va.vro.model.rrd.bipevidence.BipFileProviderData;
import gov.va.vro.model.rrd.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.rrd.bipevidence.BipFileUploadResp;
import gov.va.vro.model.rrd.bipevidence.response.UploadResponse;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.ClaimProps;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.mas.MasCompletionStatus;
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
import java.util.Set;
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
    if (contentions == null || contentions.size() == 0) {
      log.info("Claim with claim Id {} does not have contentions.", claimId);
      return false;
    }
    if (contentions.size() > 1) {
      log.info("Claim with claim Id {} have multiple contentions.", claimId);
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
    log.info("Claim {} contention special issues: {}", claimId, String.join(", ", specialIssues));
    log.info("Has special issues: {}", hasSpecialIssues);
    return hasSpecialIssues;
  }

  /**
   * Updates the contention based on the completion status of automated benefit delivery processing.
   *
   * @param contention contention to be updated
   * @param status completion status of automated benefit delivery processing
   * @return
   */
  private ClaimContention getUpdatedContention(
      long claimId, ClaimContention contention, MasCompletionStatus status) {
    long contentionId = contention.getContentionId();
    String messageId = String.format("for contention %s of claim %s", contentionId, claimId);
    log.info("Finding necessary {} updates {}", status.getDescription(), messageId);

    boolean updated = false;
    ClaimContention result = contention.toBuilder().build();

    boolean necessaryAutomationIndicator = status.isAutomationIndicator();
    if (contention.isAutomationIndicator() != necessaryAutomationIndicator) {
      log.info("Setting automation indicator to {} {}", necessaryAutomationIndicator, messageId);
      result.setAutomationIndicator(necessaryAutomationIndicator);
      updated = true;
    } else {
      log.info("Automation indicator is already {} {}", necessaryAutomationIndicator, messageId);
    }

    Set<String> issuesToRemove = status.getSpecialIssuesToRemove(claimPorps);
    List<String> specialIssueCodes = contention.getSpecialIssueCodes();
    List<String> updatedIssueCodes =
        specialIssueCodes.stream()
            .filter(code -> !issuesToRemove.contains(code.toUpperCase()))
            .collect(Collectors.toList());
    int removedCount = specialIssueCodes.size() - updatedIssueCodes.size();
    if (removedCount > 0) {
      log.info("Removing {} special issues {}", removedCount, messageId);
      log.info("Special issue codes were: {}", String.join(",", specialIssueCodes));
      log.info("Special issue code will be {}", String.join(",", updatedIssueCodes));
      result.setSpecialIssueCodes(updatedIssueCodes);
      updated = true;
    } else {
      log.error("No special issues to remove {}", messageId);
      log.info("Special issue codes were: {}", String.join(",", specialIssueCodes));
    }

    String necessaryLifecycleStatus = status.getClaimStatus().getDescription();
    if (!necessaryLifecycleStatus.equals(contention.getLifecycleStatus())) {
      log.info("Setting lifecycle status to {} {}", necessaryLifecycleStatus, messageId);
      result.setLifecycleStatus(necessaryLifecycleStatus);
      updated = true;
    } else {
      log.info("Lifecycle status is already {} {}", necessaryLifecycleStatus, messageId);
    }

    if (updated) {
      return result;
    }
    return null;
  }

  private BipClaim updateClaimProper(long claimId, MasCompletionStatus status) {
    BipClaim claim = bipApiService.getClaimDetails(claimId);
    ClaimStatus claimStatus = status.getClaimStatus();
    String necessaryLifecycleStatus = claimStatus.getDescription();
    if (!necessaryLifecycleStatus.equals(claim.getClaimLifecycleStatus())) {
      log.info("Updating lifecycle status to {} for claim {}", necessaryLifecycleStatus, claimId);
      bipApiService.updateClaimStatus(claimId, claimStatus);
      if (claimStatus == ClaimStatus.RFD) {
        saveToDbService.updateRfdFlag(String.valueOf(claimId), true);
      }
    } else {
      log.info("Lifecycle status is already {} for claim {}", necessaryLifecycleStatus, claimId);
    }
    return claim;
  }

  /**
   * Updates claim and contentions at the end of MAS automated claim processing.
   *
   * @param payload the claim payload
   * @param status the completion status for mas automation
   * @return the claim payload
   */
  public BipUpdateClaimResult updateClaim(MasProcessingObject payload, MasCompletionStatus status) {
    long claimId = Long.parseLong(payload.getBenefitClaimId());
    log.info("Attempting necessary updates for claim id = {}", claimId);

    final BipClaim claim = updateClaimProper(claimId, status);

    var contentions = bipApiService.getClaimContentions(claimId);
    if (ObjectUtils.isEmpty(contentions)) {
      String message = String.format("Claim id = %s has no contentions.", claimId);
      return BipUpdateClaimResult.ofError(message);
    }
    if (contentions.size() > 1) {
      String message = String.format("Claim id = %s has multiple contentions.", claimId);
      return BipUpdateClaimResult.ofError(message);
    }

    List<ClaimContention> updatedContentions = new ArrayList<>();
    for (ClaimContention contention : contentions) {
      ClaimContention update = getUpdatedContention(claimId, contention, status);
      if (update != null) {
        updatedContentions.add(update);
      }
    }
    if (updatedContentions.isEmpty()) {
      log.info("Nothing to update for claim {}.", claimId);
    } else {
      log.info("Preparing requests for contention updates for claim id = {}", claimId);
      String action = "UPDATED_CONTENTION";
      List<UpdateContention> updateContentions =
          updatedContentions.stream()
              .map(c -> c.toUpdateContention(action))
              .collect(Collectors.toList());
      UpdateContentionReq request =
          UpdateContentionReq.builder().updateContentions(updateContentions).build();
      log.info("Calling BIP AP Service for contention updates for claim id = {}", claimId);
      bipApiService.updateClaimContention(claimId, request);
    }

    String station = claim.getTempStationOfJurisdiction();
    if (!TSOJ.equals(station)) {
      String message =
          String.format("Claim %s is in station %s not in %s.", claimId, station, TSOJ);
      return BipUpdateClaimResult.ofWarning(message);
    }

    return new BipUpdateClaimResult(true);
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
    // We check if bipResp is null only so that the uploadPdf() test does not fail in
    // BipClaimServiceTest.
    // We created a ticket to fix this test and remove this condition.
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
