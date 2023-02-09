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

import java.util.ArrayList;
import java.util.List;

/** Mock some claim data returned by the BIP API. */
@Service
@Conditional(BipConditions.LocalEnvironmentCondition.class)
public class MockBipApiService implements IBipApiService {
  private static final long CLAIM_ID_201 = 201L;
  private static final long CLAIM_ID_204 = 204L;
  private static final long CLAIM_ID_400 = 400L;
  private static final long CLAIM_ID_401 = 401L;
  private static final long CLAIM_ID_404 = 404L;
  private static final long CLAIM_ID_500 = 500L;
  private static final long CLAIM_ID_501 = 501L;

  private static final String ERR_MSG_400 =
      "{\n"
          + "  \"messages\": [\n"
          + "    {\n"
          + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
          + "      \"key\": \"Bad reqeust\",\n"
          + "      \"severity\": \"ERROR\",\n"
          + "      \"status\": \"400\",\n"
          + "      \"text\": \"Invalid parameters.\",\n"
          + "      \"httpStatus\": \"BAD_REQUEST\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  private static final String ERR_MSG_404 =
      "{\n"
          + "  \"messages\": [\n"
          + "    {\n"
          + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
          + "      \"key\": \"bip.vetservices.claims.request.claimId.NotValid\",\n"
          + "      \"severity\": \"ERROR\",\n"
          + "      \"status\": \"404\",\n"
          + "      \"text\": \"Claim not found.\",\n"
          + "      \"httpStatus\": \"NOT_FOUND\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  private static final String ERR_MSG_401 =
      "{\n"
          + "  \"messages\": [\n"
          + "    {\n"
          + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
          + "      \"key\": \"UNAUTHORIZED\",\n"
          + "      \"severity\": \"ERROR\",\n"
          + "      \"status\": \"401\",\n"
          + "      \"text\": \"No JWT Token in Header.\",\n"
          + "      \"httpStatus\": \"UNAUTHORIZED\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  private static final String ERR_MSG_500 =
      "{\n"
          + "  \"messages\": [\n"
          + "    {\n"
          + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
          + "      \"key\": \"bip.framework.global.general.exception\",\n"
          + "      \"severity\": \"ERROR\",\n"
          + "      \"status\": \"500\",\n"
          + "      \"text\": \"Unexpected exception.\",\n"
          + "      \"httpStatus\": \"INTERNAL_SERVER_ERROR\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  private static final String ERR_MSG_501 =
      "{\n"
          + "  \"messages\": [\n"
          + "    {\n"
          + "      \"timestamp\": \"2023-01-24T16:36:59.578\",\n"
          + "      \"key\": \"system error\",\n"
          + "      \"severity\": \"ERROR\",\n"
          + "      \"status\": \"501\",\n"
          + "      \"text\": \"Not implemented.\",\n"
          + "      \"httpStatus\": \"INTERNAL_SERVER_ERROR\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  @Override
  public BipClaim getClaimDetails(long collectionId) {
    if (collectionId == 350 || collectionId == 353) {
      // valid
      return buildClaim(1234, BipClaimService.TSOJ);
    } else if (collectionId == 1001) {
      // valid
      return buildClaim(1001, BipClaimService.TSOJ);
    } else if (collectionId == 1010) {
      // valid
      return buildClaim(1010, BipClaimService.TSOJ);
    } else if (collectionId == 885491) {
      // wrong station
      return buildClaim(2010, "OTHER");
    } else if (collectionId == CLAIM_ID_404) { // invalid claim ID, throw BipException.
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (collectionId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (collectionId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.INTERNAL_SERVER_ERROR, ERR_MSG_500);
    } else {
      return buildClaim(555, BipClaimService.TSOJ);
    }
  }

  @Override
  public BipUpdateClaimResp setClaimToRfdStatus(long collectionId) throws BipException {
    if (collectionId == CLAIM_ID_404) { // invalid claim ID, throw BipException
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (collectionId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (collectionId == CLAIM_ID_400) { // not authorized, throw BipException
      throw new BipException(HttpStatus.BAD_REQUEST, ERR_MSG_400);
    } else if (collectionId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_500);
    } else if (collectionId == CLAIM_ID_501) { // 501 error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_501);
    } else {
      return new BipUpdateClaimResp(HttpStatus.OK, "OK from mock service.");
    }
  }

  @Override
  public List<ClaimContention> getClaimContentions(long claimId) throws BipException {
    if (claimId == 1234) {
      return List.of(buildContention("RDR1", "RRD"));
    } else if (claimId == 1010) {
      return List.of(buildContention("RDR1", "RRD"));
    } else if (claimId == CLAIM_ID_204) { // No data. Returns an empty list.
      return new ArrayList<>();
    } else if (claimId == CLAIM_ID_404) { // invalid claim ID, throw BipException
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (claimId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (claimId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.INTERNAL_SERVER_ERROR, ERR_MSG_500);
    } else {
      return List.of(buildContention("A", "B", "C"));
    }
  }

  @Override
  public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status)
      throws BipException {
    if (claimId == CLAIM_ID_404) { // invalid claim ID, throw BipException
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (claimId == CLAIM_ID_400) { // bad call, throw BipException
      throw new BipException(HttpStatus.BAD_REQUEST, ERR_MSG_400);
    } else if (claimId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (claimId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_500);
    } else if (claimId == CLAIM_ID_501) { // 501 error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_501);
    } else {
      return new BipUpdateClaimResp(HttpStatus.OK, "OK from mock service.");
    }
  }

  @Override
  public BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
      throws BipException {
    if (claimId == CLAIM_ID_404) { // invalid claim ID, throw BipException
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (claimId == CLAIM_ID_400) { // bad call, throw BipException
      throw new BipException(HttpStatus.BAD_REQUEST, ERR_MSG_400);
    } else if (claimId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (claimId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_500);
    } else if (claimId == CLAIM_ID_501) { // 501 error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_501);
    } else {
      return new BipUpdateClaimResp(HttpStatus.CREATED, "OK from mock service.");
    }
  }

  @Override
  public BipUpdateClaimResp addClaimContention(long claimId, CreateContentionReq contention)
      throws BipException {
    if (claimId == CLAIM_ID_404) { // invalid claim ID, throw BipException
      throw new BipException(HttpStatus.NOT_FOUND, ERR_MSG_404);
    } else if (claimId == CLAIM_ID_400) { // bad call, throw BipException
      throw new BipException(HttpStatus.BAD_REQUEST, ERR_MSG_400);
    } else if (claimId == CLAIM_ID_401) { // not authorized, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_401);
    } else if (claimId == CLAIM_ID_500) { // internal error, throw BipException
      throw new BipException(HttpStatus.UNAUTHORIZED, ERR_MSG_500);
    } else {
      String message =
          String.format("This is a mock response to create a contetion for claim %d.", claimId);
      return new BipUpdateClaimResp(HttpStatus.CREATED, message);
    }
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
