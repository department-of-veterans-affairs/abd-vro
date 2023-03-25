package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.ClaimProps;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import gov.va.vro.service.provider.bip.service.IBipCeApiService;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasCompletionStatus;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

class BipClaimServiceTest {

  private final Integer collectionId = 123;
  private final String claimId = "345";

  private ClaimProps claimProps;

  @BeforeEach
  public void setup() {
    claimProps = new ClaimProps();
    claimProps.setSpecialIssue1("RRD1");
    claimProps.setSpecialIssue2("RRD");
  }

  @Test
  void hasAnchorsWrongJurisdiction() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim("123", "King Cross"));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    assertFalse(claimService.hasAnchors(bipClaimId));
  }

  @Test
  void hasAnchorsMissingSpecialIssue() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId)).thenReturn(createClaim(claimId, "398"));
    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    assertFalse(claimService.hasAnchors(bipClaimId));
  }

  @Test
  void hasAnchors() throws BipException {

    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId)).thenReturn(createClaim(claimId, "398"));
    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of(claimProps.getSpecialIssue1(), "OTHER"))));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    assertTrue(claimService.hasAnchors(bipClaimId));
  }

  @Test
  void removeSpecialIssueMissing() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    var mpo = new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
    claimService.updateClaim(mpo, MasCompletionStatus.READY_FOR_DECISION);
  }

  @Test
  void removeSpecialIssue() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of(claimProps.getSpecialIssue1().toLowerCase(), "OTHER"))));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    var mpo = new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
    claimService.updateClaim(mpo, MasCompletionStatus.READY_FOR_DECISION);
  }

  @Test
  void completeProcessingNotRightStation() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(claimId, "Short Line"));

    BipClaimService claimService = new BipClaimService(null, bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertFalse(claimService.completeProcessing(getMpo(payload)).isTSOJ());
  }

  // TODO -> Fix this test <---
  /*
  @Test
  void completeProcessing() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId)).thenReturn(createClaim(claimId, "398"));

    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    Mockito.doNothing().when(saveToDbService).updateRfdFlag(String.valueOf(claimId), true);

    BipClaimService claimService = new BipClaimService(null, bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertTrue(claimService.markAsRfd(getMpo(payload)).isTSOJ());
    Mockito.verify(bipApiService).setClaimToRfdStatus(bipClaimId);
  }
  */
  // TODO -> Fix this test <---

  @Test
  void uploadPdf_missingData() {
    IBipCeApiService bipCeApiService = Mockito.mock(IBipCeApiService.class);
    BipClaimService claimService = new BipClaimService(null, null, bipCeApiService, null);
    var payload = MasTestData.getMasAutomatedClaimPayload();
    FetchPdfResponse fetchPdfResponse = new FetchPdfResponse();
    try {
      claimService.uploadPdf(payload, fetchPdfResponse);
      fail();
    } catch (BipException e) {
      assertEquals("PDF Response does not contain any data", e.getMessage());
    }
  }

  @Test
  void uploadPdf() {
    IBipCeApiService bipCeApiService = Mockito.mock(IBipCeApiService.class);
    BipClaimService claimService = new BipClaimService(null, null, bipCeApiService, null);
    FetchPdfResponse fetchPdfResponse = new FetchPdfResponse();
    var data = Base64.getEncoder().encode("Hello!".getBytes(StandardCharsets.UTF_8));
    fetchPdfResponse.setPdfData(new String(data));
    var payload = MasTestData.getMasAutomatedClaimPayload();
    claimService.uploadPdf(payload, fetchPdfResponse);
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

  private MasProcessingObject getMpo(MasAutomatedClaimPayload payload) {
    return new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
  }
}
