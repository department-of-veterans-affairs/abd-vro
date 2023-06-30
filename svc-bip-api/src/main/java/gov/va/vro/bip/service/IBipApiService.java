package gov.va.vro.bip.service;
import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimContention;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.UpdateContentionReq;

import java.util.List;

public interface IBipApiService {

  /**
   * getClaimDetails
   * setClaimToRfdStatus
   * updateClaimStatus
   * getClaimContentions
   * updateClaimContention
   * confirmCanCallSpecialIssueTypes
   */

  BipClaim getClaimDetails(long collectionId);
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId);
  BipUpdateClaimResp updateClaimStatus(long collectionId, ClaimStatus status);
  List<ClaimContention> getClaimContentions(long claimId);
  BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention);
  boolean confirmCanCallSpecialIssueTypes();
}
