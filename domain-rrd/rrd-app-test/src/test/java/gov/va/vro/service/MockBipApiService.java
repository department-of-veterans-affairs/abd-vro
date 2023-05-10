package gov.va.vro.service;

import gov.va.vro.model.rrd.bip.*;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Mock some claim data returned by the BIP API. */
@Service
public class MockBipApiService implements IBipApiService {
  private static final long CLAIM_ID_204 = 204L;
  private static final long CLAIM_ID_400 = 400L;
  private static final long CLAIM_ID_401 = 401L;
  private static final long CLAIM_ID_404 = 404L;
  private static final long CLAIM_ID_500 = 500L;
  private static final long CLAIM_ID_501 = 501L;

  private static final String ERR_MSG_400 =
      """
                  {
                    "messages": [
                      {
                        "timestamp": "2023-01-24T16:36:59.578",
                        "key": "Bad reqeust",
                        "severity": "ERROR",
                        "status": "400",
                        "text": "Invalid parameters.",
                        "httpStatus": "BAD_REQUEST"
                      }
                    ]
                  }""";

  private static final String ERR_MSG_404 =
      """
                  {
                    "messages": [
                      {
                        "timestamp": "2023-01-24T16:36:59.578",
                        "key": "bip.vetservices.claims.request.claimId.NotValid",
                        "severity": "ERROR",
                        "status": "404",
                        "text": "Claim not found.",
                        "httpStatus": "NOT_FOUND"
                      }
                    ]
                  }""";

  private static final String ERR_MSG_401 =
      """
                  {
                    "messages": [
                      {
                        "timestamp": "2023-01-24T16:36:59.578",
                        "key": "UNAUTHORIZED",
                        "severity": "ERROR",
                        "status": "401",
                        "text": "No JWT Token in Header.",
                        "httpStatus": "UNAUTHORIZED"
                      }
                    ]
                  }""";

  private static final String ERR_MSG_500 =
      """
                  {
                    "messages": [
                      {
                        "timestamp": "2023-01-24T16:36:59.578",
                        "key": "bip.framework.global.general.exception",
                        "severity": "ERROR",
                        "status": "500",
                        "text": "Unexpected exception.",
                        "httpStatus": "INTERNAL_SERVER_ERROR"
                      }
                    ]
                  }""";

  private static final String ERR_MSG_501 =
      """
                  {
                    "messages": [
                      {
                        "timestamp": "2023-01-24T16:36:59.578",
                        "key": "system error",
                        "severity": "ERROR",
                        "status": "501",
                        "text": "Not implemented.",
                        "httpStatus": "INTERNAL_SERVER_ERROR"
                      }
                    ]
                  }""";

  @Override
  public BipClaim getClaimDetails(long collectionId) {
    if (collectionId == 350 || collectionId == 353) {
      // valid
      return buildClaim(1234, BipClaimService.TSOJ);
    } else if (collectionId == 885491) {
      // wrong station
      return buildClaim(885491, "OTHER");
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
  public boolean verifySpecialIssueTypes() {
    return true;
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
