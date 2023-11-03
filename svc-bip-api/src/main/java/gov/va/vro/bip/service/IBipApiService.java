package gov.va.vro.bip.service;

import gov.va.vro.bip.model.BipClaimResp;
import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.UpdateContentionReq;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;

/**
 * BIP Claims API service.
 *
 * @author warren @Date 11/8/22
 */
public interface IBipApiService {

  /**
   * Gets a claim detail information.
   *
   * @param collectionId the claim ID
   * @return a BipClaim object.
   * @throws BipException error occurs.
   */
  BipClaimResp getClaimDetails(long collectionId);

  /**
   * Updates claim status to RFD.
   *
   * @param collectionId claim ID
   * @return a claim info object.
   * @throws BipException error occurs.
   */
  BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException;

  /**
   * Updates a claim status.
   *
   * @param collectionId claim ID.
   * @param status status to be updated to.
   * @return a claim update info object.
   * @throws BipException error occurs.
   */
  BipUpdateClaimResp updateClaimStatus(long collectionId, ClaimStatus status) throws BipException;

  /**
   * Gets a list of contentions in a claim.
   *
   * @param claimId claim ID.
   * @return a list of contention objects.
   * @throws BipException error occurs.
   */
  GetClaimContentionsResponse getClaimContentions(long claimId) throws BipException;

  BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention);
}
