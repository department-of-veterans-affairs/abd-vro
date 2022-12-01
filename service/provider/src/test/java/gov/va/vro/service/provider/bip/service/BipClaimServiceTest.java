package gov.va.vro.service.provider.bip.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.ClaimContention;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class BipClaimServiceTest {

  private final Integer collectionId = 123;
  private final String claimId = "345";

  @Test
  void hasAnchorsWrongJurisdiction() {

    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(collectionId))
        .thenReturn(createClaim("123", "King Cross"));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertFalse(claimService.hasAnchors(collectionId));
  }

  @Test
  void hasAnchorsMissingSpecialIssue() {

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
  void hasAnchors() {

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
  void removeSpecialIssueMissing() {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertFalse(claimService.removeSpecialIssue(Integer.parseInt(claimId)));
  }

  @Test
  void removeSpecialIssue() {
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(Integer.parseInt(claimId)))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("Rating Decision Review - Level 1", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService);
    assertTrue(claimService.removeSpecialIssue(Integer.parseInt(claimId)));
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
