package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipUpdateClaimStatusResp;
import gov.va.vro.model.bip.ClaimContention;

import java.util.HashMap;

/**
 * @author warren @Date 11/8/22
 */
public interface IBipApiService {
//    BipUpdateClaimStatusResp updateClaimStatus(String claimId, String statusCodeMsg)
//            throws BipException;
//
//    BipClaim getClaim(String claimId) throws BipException;

    HashMap<String, String> getClaimDetails(Integer collectionId) throws BipException;

    HashMap<String, String> updateClaimStatus(Integer collectionId) throws BipException;

    ClaimContention getClaimContentions(Integer claimId) throws BipException;

    HashMap<String, String> updateClaimContention(Integer claimId, ClaimContention contention) throws BipException;

    HashMap<String, String> uploadEvidence(HashMap<String, Object> uploadEvidenceReq) throws BipException;
}