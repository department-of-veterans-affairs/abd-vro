package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.*;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BipClaimService {

  public static final String TSOJ = "398";
  public static final String SPECIAL_ISSUE_1 = "rating decision review - level 1";
  public static final String SPECIAL_ISSUE_2 = "rrd";

  private final IBipApiService bipApiService;

  /**
   * Check if all the anchors for fast-tracking are satisfied.
   *
   * @param collectionId collection id identifying the claim
   * @return true if the anchors are satisfied, false otherwise
   */
  public boolean hasAnchors(int collectionId) {

    var claimDetails = bipApiService.getClaimDetails(collectionId);
    if (claimDetails == null) {
      log.warn("Claim with collection Id {} not found in BIP", collectionId);
      return false;
    }
    if (!TSOJ.equals(claimDetails.getTempStationOfJurisdiction())) {
      log.info("Claim with collection Id {} does not have TSOJ = {}", collectionId, TSOJ);
      return false;
    }
    int claimId = Integer.parseInt(claimDetails.getClaimId());
    var contentions = bipApiService.getClaimContentions(claimId);
    if (contentions == null) {
      log.info("Claim with collection Id {} does not have contentions.", collectionId);
      return false;
    }

    // collect all special issues
    var specialIssues =
        contentions.stream()
            .map(ClaimContention::getSpecialIssueCodes)
            .flatMap(Collection::stream)
            .map(String::toLowerCase) // Ignore case
            .collect(Collectors.toSet());
    return specialIssues.contains(SPECIAL_ISSUE_1) && specialIssues.contains(SPECIAL_ISSUE_2);
  }

  /**
   * Remove special issue contention.
   *
   * @param payload the claim payload
   * @return the claim payload
   */
  public MasProcessingObject removeSpecialIssue(MasProcessingObject payload) {
    var claimId = Long.parseLong(payload.getClaimId());
    log.info("Attempting to remove special issue for claim id = {}", claimId);

    var contentions = bipApiService.getClaimContentions(claimId);
    if (ObjectUtils.isEmpty(contentions)) {
      log.warn("Claim id = {} has no contentions.", claimId);
      return payload;
    }

    List<ClaimContention> updatedContentions = new ArrayList<>();
    for (ClaimContention contention : contentions) {
      var codes =
          contention.getSpecialIssueCodes().stream()
              .map(String::toLowerCase)
              .collect(Collectors.toSet());
      if (codes.contains(SPECIAL_ISSUE_1)) {
        // remove string from contention
        List<String> updatedCodes =
            contention.getSpecialIssueCodes().stream()
                .filter(code -> !SPECIAL_ISSUE_1.equalsIgnoreCase(code))
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
    String action = "Remove special issue";
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
    int collectionId = payload.getCollectionId();
    log.info("Marking claim with collectionId = {} as Ready For Decision", collectionId);

    var response = bipApiService.updateClaimStatus(collectionId, ClaimStatus.RFD);
    // TODO: check response, catch exceptions etc
    return payload;
  }

  /**
   * Check if claim is still eligible for fast tracking, and if so, update status.
   *
   * @param payload the claim payload
   * @return true if the status is updated, false otherwise
   */
  public boolean completeProcessing(MasAutomatedClaimPayload payload) {
    int collectionId = payload.getCollectionId();

    // check again if TSOJ. If not, abandon route
    var claim = bipApiService.getClaimDetails(collectionId);
    if (!TSOJ.equals(claim.getTempStationOfJurisdiction())) {
      log.info(
          "Claim with collection Id = {} is in state {}. Not updating status",
          collectionId,
          claim.getTempStationOfJurisdiction());
      return false;
    }
    // otherwise, update claim
    log.info("Updating claim status for claim with collection id = {}", collectionId);
    bipApiService.setClaimToRfdStatus(collectionId);
    return true;
  }

  public FetchPdfResponse uploadPdf(FetchPdfResponse pdfResponse) {
    log.info("Uploading pdf for claim {}...", pdfResponse.getClaimSubmissionId());
    String filename = String.format("temp_evidence-%s.pdf", pdfResponse.getClaimSubmissionId());
    File file = null;
    try {
      file = File.createTempFile(filename, "tmp", null);
      byte[] decoder = Base64.getDecoder().decode(pdfResponse.getPdfData());
      InputStream is = new ByteArrayInputStream(decoder);
      Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      // TODO: I don't know what parameters should be passed here. A BIP expert will address this
      bipApiService.uploadEvidence(
          FileIdType.FILENUMBER,
          pdfResponse.getClaimSubmissionId(),
          new BipFileUploadPayload(),
          file);
      return pdfResponse;
    } catch (IOException ioe) {
      throw new BipException("Failed to upload evidence file.", ioe);
    } finally {
      if (file != null) {
        file.delete();
      }
    }
  }
}
