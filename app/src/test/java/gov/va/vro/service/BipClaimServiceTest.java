package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.UpdateContention;
import gov.va.vro.model.bip.UpdateContentionReq;
import gov.va.vro.model.mas.ClaimDetail;
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
import gov.va.vro.service.spi.db.SaveToDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

class BipClaimServiceTest {

  private static final Integer collectionId = 123;
  private static final String CLAIM_ID1 = "345";
  private static final String CLAIM_ID2 = "39208503";

  private static final long CONTENTION_ID = 1000L;
  private static final List<String> SPECIAL_ISSUES = Arrays.asList("RRD", "RRD1");
  private static final ClaimStatus CONTENTION_STATUS = ClaimStatus.OPEN;
  private static final boolean AUTU_IND = false;

  private ClaimProps claimProps;

  @BeforeEach
  public void setup() {
    claimProps = new ClaimProps();
    claimProps.setSpecialIssue1("RRD1");
    claimProps.setSpecialIssue2("RRD");
  }

  @Test
  void hasAnchorsWrongJurisdiction() throws BipException {
    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim("123", "King Cross"));

    BipClaimService claimService = new BipClaimService(claimProps, bipApiService, null, null);
    assertFalse(claimService.hasAnchors(bipClaimId));
  }

  @Test
  void hasAnchorsMissingSpecialIssue() throws BipException {
    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(CLAIM_ID1, "398"));
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

    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(CLAIM_ID1, "398"));
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
    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(CLAIM_ID1, "Short Line"));

    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of("RRD", "OTHER"))));

    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    Mockito.doNothing().when(saveToDbService).updateRfdFlag(CLAIM_ID1, true);

    BipClaimService claimService =
        new BipClaimService(claimProps, bipApiService, null, saveToDbService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", CLAIM_ID1);
    var mpo = new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
    claimService.updateClaim(mpo, MasCompletionStatus.READY_FOR_DECISION);
  }

  @Test
  void removeSpecialIssue() throws BipException {
    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);

    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(CLAIM_ID1, "Short Line"));

    Mockito.when(bipApiService.getClaimContentions(bipClaimId))
        .thenReturn(
            List.of(
                createContention(List.of("TEST", "RRD")),
                createContention(List.of(claimProps.getSpecialIssue1().toLowerCase(), "OTHER"))));

    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    Mockito.doNothing().when(saveToDbService).updateRfdFlag(CLAIM_ID1, true);

    BipClaimService claimService =
        new BipClaimService(claimProps, bipApiService, null, saveToDbService);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", CLAIM_ID1);
    var mpo = new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
    claimService.updateClaim(mpo, MasCompletionStatus.READY_FOR_DECISION);
  }

  @Test
  void completeProcessingNotRightStation() throws BipException {
    long bipClaimId = Long.parseLong(CLAIM_ID1);
    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(bipClaimId))
        .thenReturn(createClaim(CLAIM_ID1, "Short Line"));

    BipClaimService claimService = new BipClaimService(null, bipApiService, null, null);
    var payload = MasTestData.getMasAutomatedClaimPayload(collectionId, "1701", CLAIM_ID1);
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

  @Test
  void testUpdateClaim() {
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setBenefitClaimId(CLAIM_ID1);
    MasAutomatedClaimPayload claimPayload =
        MasAutomatedClaimPayload.builder().claimDetail(claimDetail).build();

    MasProcessingObject payload =
        new MasProcessingObject(claimPayload, MasCamelStage.START_COMPLETE);

    ClaimContention contention = new ClaimContention();
    contention.setContentionId(CONTENTION_ID);
    contention.setSpecialIssueCodes(SPECIAL_ISSUES);
    contention.setLifecycleStatus(CONTENTION_STATUS.getDescription());
    contention.setAutomationIndicator(AUTU_IND);
    String action = "UPDATED_CONTENTION";
    UpdateContention updateContention = contention.toUpdateContention(action);
    List<UpdateContention> updateContentions = Collections.singletonList(updateContention);

    UpdateContentionReq request =
        UpdateContentionReq.builder().updateContentions(updateContentions).build();

    long claimID1 = Long.parseLong(CLAIM_ID1);
    long claimID2 = Long.parseLong(CLAIM_ID2);
    BipClaim claim1 = new BipClaim();
    claim1.setClaimId(CLAIM_ID1);
    claim1.setClaimLifecycleStatus(ClaimStatus.OPEN.getDescription());
    BipClaim claim2 = new BipClaim();
    claim2.setClaimId(CLAIM_ID2);
    claim2.setClaimLifecycleStatus(ClaimStatus.RI.getDescription());

    BipUpdateClaimResp bipUpdateClaimResp = new BipUpdateClaimResp();
    bipUpdateClaimResp.setMessage("done");
    bipUpdateClaimResp.setStatus(HttpStatus.OK);

    IBipApiService bipApiService = Mockito.mock(IBipApiService.class);
    Mockito.when(bipApiService.getClaimDetails(claimID1)).thenReturn(claim1);
    Mockito.when(bipApiService.getClaimDetails(claimID2)).thenReturn(claim2);
    Mockito.when(bipApiService.getClaimContentions(claimID1))
        .thenReturn(Collections.singletonList(contention));
    Mockito.when(bipApiService.getClaimContentions(claimID2)).thenReturn(new ArrayList<>());

    Mockito.when(bipApiService.updateClaimContention(anyLong(), any()))
        .thenReturn(bipUpdateClaimResp);

    ClaimProps claimProp = new ClaimProps();
    claimProp.setSpecialIssue1(SPECIAL_ISSUES.get(0));
    claimProp.setSpecialIssue2(SPECIAL_ISSUES.get(1));

    SaveToDbService saveToDbService = Mockito.mock(SaveToDbService.class);
    Mockito.doNothing().when(saveToDbService).updateRfdFlag(anyString(), anyBoolean());

    BipClaimService claimService =
        new BipClaimService(claimProp, bipApiService, null, saveToDbService);

    for (MasCompletionStatus status : MasCompletionStatus.values()) {
      MasProcessingObject result = claimService.updateClaim(payload, status);
      assertEquals(payload, result);
    }
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
