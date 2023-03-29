package gov.va.vro.service;

import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.NEW_NOT_PRESUMPTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MasProcessingServiceTest extends BaseIntegrationTest {

  @Autowired MasProcessingService masProcessingService;

  @Autowired ClaimSubmissionRepository claimSubmissionRepository;

  @Test
  void testClaimPersistence() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "71";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    masProcessingService.processIncomingClaimSaveToDB(request1);

    var claimEntity1 = verifyClaimPersisted(request1);
    var contentions = claimEntity1.getContentions();
    assertEquals(1, contentions.size());
    var contention = contentions.get(0);
    assertEquals(request1.getDiagnosticCode(), contention.getDiagnosticCode());
    // same claim, different diagnostic code
    var diagnosticCode2 = "17";
    var request2 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode2, claimId1);
    masProcessingService.processIncomingClaimSaveToDB(request2);
    var claimEntity2 = verifyClaimPersisted(request2);
    contentions = claimEntity2.getContentions();
    ClaimEntity claim = claimRepository.findByVbmsId(claimId1).orElseThrow();
    assertEquals(2, contentions.size());

    // new claim
    var claimId2 = "321";
    var request3 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId2);
    masProcessingService.processIncomingClaimSaveToDB(request3);
    verifyClaimPersisted(request3);
  }

  @Test
  public void testNotInScope() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "71";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    var response1 = masProcessingService.processIncomingClaimGetUnprocessableReason(request1);
    // wrong diagnostic code
    assertEquals(
        "Claim with [collection id = 123], [diagnostic code = 71], and"
            + " [disability action type = INCREASE] is not in scope.",
        response1);

    var request2 = MasTestData.getMasAutomatedClaimPayload(collectionId1, "7101", claimId1);
    request2.getClaimDetail().getConditions().setDisabilityActionType("OTHER");
    var response2 = masProcessingService.processIncomingClaimGetUnprocessableReason(request2);
    // wrong disability action
    assertEquals(
        "Claim with [collection id = 123], [diagnostic code = 7101], and"
            + " [disability action type = OTHER] is not in scope.",
        response2);
  }

  @Test
  public void testInScopeButNotPresumptive() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "7101";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    request1.getClaimDetail().getConditions().setDisabilityActionType("NEW");
    var response = masProcessingService.getOffRampReasonPresumptiveCheck(request1);
    assertEquals(NEW_NOT_PRESUMPTIVE, response.get());
  }

  @Test
  public void testInScopeAndPresumptiveButMissingAnchors() {
    var collectionId = 123;
    var claimId = "123";
    var diagnosticCode = "7101";
    var request = MasTestData.getMasAutomatedClaimPayload(collectionId, diagnosticCode, claimId);
    request.getClaimDetail().getConditions().setDisabilityActionType("NEW");
    request = request.toBuilder().veteranFlashIds(List.of("123", "266")).build();
    var response = masProcessingService.processIncomingClaimGetUnprocessableReason(request);
    assertEquals(
        "Claim with [collection id = 123] does not qualify for "
            + "automated processing because it is missing anchors.",
        response);
  }

  private ClaimEntity verifyClaimPersisted(MasAutomatedClaimPayload request) {
    var claim = claimRepository.findByVbmsId(request.getBenefitClaimId()).orElseThrow();
    var claimSubmissionList =
        claimSubmissionRepository.findByReferenceIdAndIdType(
            String.valueOf(request.getCollectionId()), MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
    assertTrue(claimSubmissionList.size() > 0);
    for (ClaimSubmissionEntity submission : claimSubmissionList) {
      assertEquals(request.getCollectionId().toString(), submission.getReferenceId());
    }

    assertEquals(request.getVeteranIcn(), claim.getVeteran().getIcn());
    return claim;
  }
}
