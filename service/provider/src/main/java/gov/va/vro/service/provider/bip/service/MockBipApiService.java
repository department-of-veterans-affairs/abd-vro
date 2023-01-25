package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.CreateContentionReq;
import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.service.provider.bip.BipException;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/** Mock some claim data returned by the BIP API. */
@Service
@Conditional(BipConditions.LocalEnvCondition.class)
public class MockBipApiService implements IBipApiService {
  @Override
  public BipClaim getClaimDetails(long collectionId) {
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
  public BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK from mock service.");
  }

  @Override
  public List<ClaimContention> getClaimContentions(long claimId) throws BipException {
    if (claimId == 1234) {
      return List.of(
          buildContention(BipClaimService.SPECIAL_ISSUE_1, BipClaimService.SPECIAL_ISSUE_2));
    }
    return List.of(buildContention("A", "B", "C"));
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status)
      throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK from mock service.");
  }

  @Override
  public BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
      throws BipException {
    return new BipUpdateClaimResp(HttpStatus.OK, "OK from mock service.");
  }

  @Override
  public BipUpdateClaimResp addClaimContention(long claimId, CreateContentionReq contention)
      throws BipException {
    String message =
        String.format("This is a mock response to create a contetion for claim %d.", claimId);
    return new BipUpdateClaimResp(HttpStatus.OK, message);
  }

  @Override
  public BipFileUploadResp uploadEvidence(
      FileIdType idtype, String fileId, BipFileUploadPayload uploadEvidenceReq, File file)
      throws BipException {
    String message =
        String.format(
            "This is a mock response to upload evidence file for %s.",
            idtype.name() + ":" + fileId);
    return new BipFileUploadResp(HttpStatus.OK, message);
  }

  @Override
  public BipFileUploadResp uploadEvidenceFile(
      FileIdType fileIdType, String fileId, BipFileUploadPayload payload, byte[] fileContent)
      throws BipException {
    String message =
        String.format(
            "This is a mock response to upload multipart file for %s.",
            fileIdType.name() + ":" + fileId);
    return new BipFileUploadResp(HttpStatus.OK, message);
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
