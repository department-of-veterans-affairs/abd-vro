package gov.va.vro.bip.service;

import gov.va.vro.bip.model.cancel.CancelClaimRequest;
import gov.va.vro.bip.model.cancel.CancelClaimResponse;
import gov.va.vro.bip.model.claim.GetClaimResponse;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.CreateClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetClaimContentionsResponse;
import gov.va.vro.bip.model.contentions.GetSpecialIssueTypesResponse;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsRequest;
import gov.va.vro.bip.model.contentions.UpdateClaimContentionsResponse;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleRequest;
import gov.va.vro.bip.model.lifecycle.PutClaimLifecycleResponse;
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
   * Updates a claim's lifecycle status.
   *
   * @param request request
   * @return response
   * @throws BipException error occurs
   */
  PutClaimLifecycleResponse putClaimLifecycleStatus(PutClaimLifecycleRequest request);

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

  /**
   * Gets a list of all special issue types.
   *
   * @param request request
   * @return response
   * @throws BipException error occurs
   */
  GetSpecialIssueTypesResponse getSpecialIssueTypes();
}
