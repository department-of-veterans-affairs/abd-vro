package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.*;
import gov.va.vro.service.provider.bip.BipException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/** @author warren @Date 11/8/22 */
public interface IBipApiService {
  BipClaim getClaimDetails(Integer collectionId) throws BipException;

  BipUpdateClaimResp updateClaimStatus(Integer collectionId, String status) throws BipException;

  List<ClaimContention> getClaimContentions(Integer claimId) throws BipException;

  BipUpdateClaimResp updateClaimContention(Integer claimId, UpdateContentionReq contention)
      throws BipException;

  HashMap<String, String> uploadEvidence(
      String fileId, BipFileUploadPayload uploadEvidenceReq, File file) throws BipException;
}
