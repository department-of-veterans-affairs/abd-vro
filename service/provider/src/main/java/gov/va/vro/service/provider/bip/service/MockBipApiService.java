package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.*;
import gov.va.vro.service.provider.bip.BipException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MockBipApiService implements IBipApiService {
  @Override
  public BipClaim getClaimDetails(Integer collectionId) throws BipException {
    return null;
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(Integer collectionId, String status)
      throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK");
  }

  @Override
  public List<ClaimContention> getClaimContentions(Integer claimId) throws BipException {
    return null;
  }

  @Override
  public BipUpdateClaimResp updateClaimContention(Integer claimId, UpdateContentionReq contention)
      throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK");
  }

  @Override
  public Map<String, String> uploadEvidence(
      String fileId, BipFileUploadPayload uploadEvidenceReq, File file) throws BipException {
    return Collections.emptyMap();
  }

  private BipClaim buildClaim(int claimId, String station) {
    var claim = new BipClaim();
    claim.setClaimId(Integer.toString(claimId));
    claim.setTempStationOfJurisdiction(station);
    return claim;
  }
}
