package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.ClaimContention;

import java.util.HashMap;
import java.util.List;

/** @author warren @Date 11/8/22 */
public interface IBipApiService {
  HashMap<String, String> getClaimDetails(Integer collectionId) throws BipException;

  HashMap<String, String> updateClaimStatus(Integer collectionId) throws BipException;

  List<ClaimContention> getClaimContentions(Integer claimId) throws BipException;

  HashMap<String, String> updateClaimContention(Integer claimId, ClaimContention contention)
      throws BipException;

  HashMap<String, String> uploadEvidence(HashMap<String, Object> uploadEvidenceReq)
      throws BipException;
}
