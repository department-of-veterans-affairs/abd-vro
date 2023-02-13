package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.bip.service.IBipApiService;
import gov.va.vro.service.provider.bip.service.IBipCeApiService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.spi.db.SaveToDbService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

class BipClaimServiceTest {

  private final Integer collectionId = 123;
  private final String claimId = "345";

  @Test
  void hasAnchorsWrongJurisdiction() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim("123", "King Cross"));

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
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

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
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
                createContention(List.of("Rating Decision Review - Level 1", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
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

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    var mpo = new MasProcessingObject();
    mpo.setClaimPayload(payload);
    claimService.removeSpecialIssue(mpo);
  }

  @Test
  void removeSpecialIssue() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("Rating Decision Review - Level 1", "OTHER"))));

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    var mpo = new MasProcessingObject();
    mpo.setClaimPayload(payload);
    claimService.removeSpecialIssue(mpo);

    Mockito.verify(bipApiService)
        .updateClaimContention(Mockito.anyLong(), Mockito.any(UpdateContentionReq.class));
  }

  @Test
  void completeProcessingNotRightStation() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(claimId, "Short Line"));

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertFalse(claimService.completeProcessing(getMpo(payload)).isTSOJ());
  }

  @Test
  void completeProcessing() throws BipException {
    long bipClaimId = Long.parseLong(claimId);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId)).thenReturn(createClaim(claimId, "398"));

    BipClaimService claimService = new BipClaimService(bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", claimId);
    assertTrue(claimService.completeProcessing(getMpo(payload)).isTSOJ());
    Mockito.verify(bipApiService).setClaimToRfdStatus(bipClaimId);
  }

  @Test
  void uploadPdf_missingData() {
    IBipCeApiService bipCeApiService = Mockito.mock(IBipCeApiService.class);
    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    BipClaimService claimService = new BipClaimService(null, bipCeApiService, saveToDbService);
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
    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    BipClaimService claimService = new BipClaimService(null, bipCeApiService, saveToDbService);
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
    var mpo = new MasProcessingObject();
    mpo.setClaimPayload(payload);
    return mpo;
  }
}
