package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
  public static final String STATUS_READY = "RFD";
  public static final String STATUS_DECISION_COMPLETE = "Rating Decision Complete";

  private final IBipApiService bipApiService;

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

  public MasAutomatedClaimPayload removeSpecialIssue(MasAutomatedClaimPayload payload) {
    var claimId = payload.getClaimId();
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
    var request = new UpdateContentionReq(updatedContentions);
    bipApiService.updateClaimContention(claimId, request);
    return payload;
  }

  public MasAutomatedClaimPayload markAsRFD(MasAutomatedClaimPayload payload) {
    int collectionId = payload.getCollectionId();
    log.info("Marking claim with collectionId = {} as Ready For Decision", collectionId);
    var response = bipApiService.updateClaimStatus(collectionId, STATUS_READY);
    // TODO: check response, catch exceptions etc
    return payload;
  }

  // markAsRFID = sufficiencyFlag (from Health Assessment BP service)
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
    bipApiService.updateClaimStatus(collectionId, STATUS_DECISION_COMPLETE);
    return true;
  }
}
