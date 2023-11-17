package gov.va.vro.bip.service;

import gov.va.vro.bip.model.BipUpdateClaimResp;
import gov.va.vro.bip.model.ClaimStatus;
import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionRequest;
import gov.va.vro.bip.model.tsoj.PutTempStationOfJurisdictionResponse;

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
  GetClaimResponse getClaimDetails(long collectionId);

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
   * Gets a list of contention summaries in a claim.
   *
   * @param claimId claim ID.
   * @return response
   * @throws BipException error occurs.
   */
  GetClaimContentionsResponse getClaimContentions(long claimId) throws BipException;

  /**
   * Create one or more contentions in a claim.
   *
   * @param request request
   * @return response
   * @throws BipException error occurs
   */
  CreateClaimContentionsResponse createClaimContentions(CreateClaimContentionsRequest request);

  /**
   * Updates one or more existing contentions in a claim.
   *
   * @param request request
   * @return response
   * @throws BipException error occurs
   */
  UpdateClaimContentionsResponse updateClaimContentions(UpdateClaimContentionsRequest request);

  /**
   * Cancels a claim record.
   *
   * @param request model defining the details of the cancellation requested.
   * @return a contention response object now containing the updated contention information.
   * @throws BipException error occurs.
   */
  CancelClaimResponse cancelClaim(CancelClaimRequest request);

  /**
   * Sets the temporary station of jurisdiction for a claim.
   *
   * @param request request
   * @return response
   * @throws BipException error occurs
   */
  PutTempStationOfJurisdictionResponse putTempStationOfJurisdiction(
      PutTempStationOfJurisdictionRequest request);
}
