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
    if (collectionId == 350 || collectionId == 353) {
      // valid
      return buildClaim(1234, BipClaimService.TSOJ);
    } else if (collectionId == 351) {
      // wrong station
      return buildClaim(999, "OTHER");
    } else {
      return buildClaim(555, BipClaimService.TSOJ);
    }
  }

  @Override
  public List<ClaimContention> getClaimContentions(Integer claimId) throws BipException {
    if (claimId == 1234) {
      return List.of(
          buildContention(BipClaimService.SPECIAL_ISSUE_1, BipClaimService.SPECIAL_ISSUE_2));
    }
    return List.of(buildContention("A", "B", "C"));
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(Integer collectionId, String status)
      throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK");
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

  private ClaimContention buildContention(String... specialIssueCodes) {
    var contention = new ClaimContention();
    contention.setSpecialIssueCodes(List.of(specialIssueCodes));
    return contention;
  }
}
