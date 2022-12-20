package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class BipClaimServiceTest {

  private final Integer collectionId = 123;
  private final String claimId = "345";

  @Test
  void hasAnchorsWrongJurisdiction() throws BipException {

    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim("123", "King Cross"));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertFalse(claimService.hasAnchors(collectionId));
  }

  @Test
  void hasAnchorsMissingSpecialIssue() throws BipException {

    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim(claimId, "398"));
    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertFalse(claimService.hasAnchors(collectionId));
  }

  @Test
  void hasAnchors() throws BipException {

    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim(claimId, "398"));
    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("Rating Decision Review - Level 1", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertTrue(claimService.hasAnchors(collectionId));
  }

  @Test
  void removeSpecialIssueMissing() throws BipException {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    claimService.removeSpecialIssue(payload);
    // TODO: Verify result
  }

  @Test
  void removeSpecialIssue() throws BipException {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("Rating Decision Review - Level 1", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    claimService.removeSpecialIssue(payload);
    // TODO: Verify arguments passed
    Mockito.verify(bipApiService)
        .updateClaimContention(Mockito.anyLong(), Mockito.any(UpdateContentionReq.class));
  }

  @Test
  void completeProcessingNotRightStation() throws BipException {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim(claimId, "Short Line"));

    BipClaimService claimService = new BipClaimService(bipApiService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertFalse(claimService.completeProcessing(payload));
  }

  @Test
  void completeProcessing() throws BipException {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim(claimId, "398"));

    BipClaimService claimService = new BipClaimService(bipApiService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertTrue(claimService.completeProcessing(payload));
    Mockito.verify(bipApiService).setClaimToRfdStatus(collectionId);
  }

  private ClaimContention createContention(List<String> codes) {
    var contention = new ClaimContention();
    contention.setSpecialIssueCodes(codes);
    return contention;
  }

  private BipClaim createClaim(String claimId, String station) {
    var claim = new BipClaim();
    claim.setClaimId(claimId);
    claim.setTempStationOfJurisdiction(station);
    return claim;
  }
}
