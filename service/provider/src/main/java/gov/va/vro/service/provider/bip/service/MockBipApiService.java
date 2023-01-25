package gov.va.vro.service.provider.bip.service;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.CreateContentionReq;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.bip.BipException;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    } else if (collectionId < 100000) {
      return buildClaim(555, BipClaimService.TSOJ);
    } else { // invalid, throw BipException
      String errMsg =
          "{\n"
              + "  \"messages\": [\n"
              + "    {\n"
              + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
              + "      \"key\": \"bip.vetservices.claims.request.claimId.NotValid\",\n"
              + "      \"severity\": \"ERROR\",\n"
              + "      \"status\": \"404\",\n"
              + "      \"text\": \"ClaimInfoRequest.claimId is not valid\",\n"
              + "      \"httpStatus\": \"NOT_FOUND\"\n"
              + "    }\n"
              + "  ]\n"
              + "}";
      throw new BipException(HttpStatus.NOT_FOUND, errMsg);
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
