package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bip.BipClaim;
import gov.va.vro.model.bip.BipUpdateClaimResp;
import gov.va.vro.model.bip.ClaimContention;
import gov.va.vro.model.bip.ClaimStatus;
import gov.va.vro.model.bip.UpdateContention;
import gov.va.vro.model.bip.UpdateContentionReq;
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
import org.jetbrains.annotations.NotNull;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class BipClaimServiceTest {

  private static final Integer collectionId = 123;
  private static final String CLAIM_ID1 = "345";
  private static final String CLAIM_ID2 = "39208503";

  private static final long CONTENTION_ID = 1000L;
  private static final long CONTENTION_ID2 = 1001L;
  private static final List<String> SPECIAL_ISSUES = Arrays.asList("RRD", "RRD1");
  private static final ClaimStatus CONTENTION_STATUS = ClaimStatus.OPEN;
  private static final boolean AUTU_IND = false;

  private ClaimProps claimProps;

  private Map<Long, BipClaim> bipClaims = new ConcurrentHashMap<>();
  private Map<Long, List<ClaimContention>> bipContentions = new ConcurrentHashMap<>();

  class BipApiTestService implements IBipApiService {

    @Override
    public BipClaim getClaimDetails(long claimId) throws BipException {
      return bipClaims.get(claimId);
    }

    @Override
    public BipUpdateClaimResp setClaimToRfdStatus(long claimId) throws BipException {
      return updateClaimStatus(claimId, ClaimStatus.RFD);
    }

    @Override
    public BipUpdateClaimResp updateClaimStatus(long claimId, ClaimStatus status)
        throws BipException {
      BipClaim claim = getBipClaim(claimId);
      try {
        claim.setClaimLifecycleStatus(status.getDescription());
        return new BipUpdateClaimResp(HttpStatus.OK, "successful");
      } catch (Exception e) {
        throw new BipException("failed to update claim status");
      }
    }

    @Override
    public List<ClaimContention> getClaimContentions(long claimId) throws BipException {
      //      BipClaim claim = getBipClaim(claimId);
      List<ClaimContention> contentions = bipContentions.get(claimId);
      if (contentions == null) {
        return new ArrayList<>();
      }
      return contentions;
    }

    @NotNull
    private BipClaim getBipClaim(long claimId) {
      BipClaim claim = bipClaims.get(claimId);
      if (claim == null) {
        throw new BipException("claim not found.");
      }
      return claim;
    }

    @Override
    public BipUpdateClaimResp updateClaimContention(long claimId, UpdateContentionReq contention)
        throws BipException {
      List<ClaimContention> contentions = bipContentions.get(claimId);
      if (contentions == null) {
        throw new BipException("Contention to update is not found.");
      }
      Map<Long, UpdateContention> updtContentions =
          contention.getUpdateContentions().stream()
              .collect(Collectors.toMap(c -> c.getContentionId(), c -> c));
      contentions.forEach(
          c -> {
            UpdateContention updt = updtContentions.get(c.getContentionId());
            if (updt != null) {
              c.setLifecycleStatus(updt.getLifecycleStatus());
              c.setAutomationIndicator(updt.isAutomationIndicator());
              c.setSpecialIssueCodes(updt.getSpecialIssueCodes());
            }
          });
      return new BipUpdateClaimResp(HttpStatus.OK, "updated");
    }

    @Override
    public boolean verifySpecialIssueTypes() {
      return true;
    }
  }

  private void initializeBipClaims() {
    BipClaim claim = new BipClaim();
    claim.setClaimId(CLAIM_ID1);
    claim.setClaimLifecycleStatus(ClaimStatus.OPEN.getDescription());
    bipClaims.put(Long.parseLong(CLAIM_ID1), claim);

    claim = new BipClaim();
    claim.setClaimId(CLAIM_ID2);
    claim.setClaimLifecycleStatus(ClaimStatus.RFD.getDescription());
    bipClaims.put(Long.parseLong(CLAIM_ID2), claim);

    ClaimContention contention = new ClaimContention();
    contention.setContentionId(CONTENTION_ID);
    contention.setSpecialIssueCodes(SPECIAL_ISSUES);
    contention.setLifecycleStatus(ClaimStatus.OPEN.getDescription());
    contention.setAutomationIndicator(false);
    bipContentions.put(Long.parseLong(CLAIM_ID1), Collections.singletonList(contention));

    contention = new ClaimContention();
    contention.setContentionId(CONTENTION_ID2);
    contention.setSpecialIssueCodes(SPECIAL_ISSUES);
    contention.setLifecycleStatus(ClaimStatus.RFD.getDescription());
    contention.setAutomationIndicator(true);
    bipContentions.put(Long.parseLong(CLAIM_ID2), Collections.singletonList(contention));
  }

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
    assertFalse(claimService.hasAnchors(bipClaimId));
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
