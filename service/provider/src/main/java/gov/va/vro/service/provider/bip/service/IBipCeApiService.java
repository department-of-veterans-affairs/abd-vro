package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.service.provider.bip.BipException;

/** BIP Claims Evidence API service. */
public interface IBipCeApiService {
  /**
   * Uploads an evidence file for the claim.
   *
   * @param idType ID type. It should be FILENUMBER, SSN, ARTICIPANT_ID, or EDIPI
   * @param fileId id
   * @param payload upload payload data.
   * @param fileContent the file to be uploaded.
   * @return an object for the upload status.
   * @throws BipException exception
   */
  BipFileUploadResp uploadEvidenceFile(
      FileIdType idType, String fileId, BipFileUploadPayload payload, byte[] fileContent)
      throws BipException;

  /**
   * Verifies a call to the BIP Claim Evidence API can be made by getting document types.
   *
   * @return boolean verification status
   */
  boolean verifyDocumentTypes();
}
