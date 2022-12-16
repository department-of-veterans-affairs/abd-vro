package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipFileUploadPayload;
import gov.va.vro.model.bip.BipFileUploadResp;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.CreateContentionReq;
import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.bip.BipException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * BIP Claims and Evidence API services.
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

  /**
   * Adds a contention for a claim.
   *
   * @param claimId claim
   * @param contention contention to be added.
   * @return an update status information object.
   * @throws BipException error occurs.
   */
  BipUpdateClaimResp addClaimContention(long claimId, CreateContentionReq contention)
      throws BipException;

  /**
   * Uploads a file for the claim.
   *
   * @param idtype ID type. It should be FILENUMBER, SSN, ARTICIPANT_ID, or EDIPI
   * @param fileId id
   * @param uploadEvidenceReq upload payload data.
   * @param file file to be uploaded.
   * @return an object for the upload status.
   * @throws BipException exception
   */
  BipFileUploadResp uploadEvidence(
      FileIdType idtype, String fileId, BipFileUploadPayload uploadEvidenceReq, File file)
      throws BipException;

  /**
   * Uploads an evidence file for the claim.
   *
   * @param idtype ID type. It should be FILENUMBER, SSN, ARTICIPANT_ID, or EDIPI
   * @param fileId id
   * @param uploadEvidenceReq upload payload data.
   * @param file the file to be uploaded.
   * @return an object for the upload status.
   * @throws BipException exception
   */
  BipFileUploadResp uploadEvidenceFile(
      FileIdType idtype, String fileId, BipFileUploadPayload uploadEvidenceReq, MultipartFile file)
      throws BipException;
}
