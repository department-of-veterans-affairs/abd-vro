package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.bip.BipException;

import java.util.List;

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
  BipClaim getClaimDetails(long collectionId) throws BipException;

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
  List<ClaimContention> getClaimContentions(long claimId) throws BipException;

  /**
   * Updates a contention in a cloim.
   *
   * @param claimId claim ID.
   * @param contention updated contention.
   * @return an object with the information of update status and a message.
   * @throws BipException error occurs.
   */
  BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
      throws BipException;

  boolean verifySpecialIssueTypes();
}
