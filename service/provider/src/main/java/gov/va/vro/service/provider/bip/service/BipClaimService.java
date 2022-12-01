package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BipClaimService {

  private static final String TSOJ = "398";
  private static final String SPECIAL_ISSUE_1 = "Rating Decision Review - Level 1";
  private static final String SPECIAL_ISSUE_2 = "RRD";

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
            .collect(Collectors.toSet());
    return specialIssues.contains(SPECIAL_ISSUE_1) && specialIssues.contains(SPECIAL_ISSUE_2);
  }

  public boolean removeSpecialIssue(int claimId) {
    var contentions = bipApiService.getClaimContentions(claimId);

    List<ClaimContention> updatedContentions = new ArrayList<>();
    for (ClaimContention contention : contentions) {
      List<String> codes = contention.getSpecialIssueCodes();
      if (codes.contains(SPECIAL_ISSUE_1)) {
        List<String> updatedCodes =
            codes.stream()
                .filter(code -> !SPECIAL_ISSUE_1.equals(code))
                .collect(Collectors.toList());
        var update = contention.toBuilder().specialIssueCodes(updatedCodes).build();
        updatedContentions.add(update);
      }
    }
    if (updatedContentions.isEmpty()) {
      return false; // nothing to update
    }
    var request = new UpdateContentionReq(updatedContentions);
    bipApiService.updateClaimContention(claimId, request);
    return true;
  }

  public boolean markAsRFD(int claimId) {
    // TODO: check if markAsRFD?
    // TODO: If yes, call markClaimAsRFD
    // in either case check station and FI
    checkStationAndFI(claimId);
    return false;
  }

  public void checkStationAndFI(int claimId) {
    // TODO: ???
  }

  public void updateClaim(int claimId) {
    // TODO: ???
  }
}
