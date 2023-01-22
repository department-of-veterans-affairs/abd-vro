package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.MasTestData;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MasProcessingServiceTest extends BaseIntegrationTest {

  @Autowired MasProcessingService masProcessingService;

  @Test
  void testClaimPersistence() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "71";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    masProcessingService.processIncomingClaim(request1);

    var claimEntity1 = verifyClaimPersisted(request1);
    var contentions = claimEntity1.getContentions();
    assertEquals(1, contentions.size());
    var contention = contentions.get(0);
    assertEquals(request1.getDiagnosticCode(), contention.getDiagnosticCode());
    // same claim, different diagnostic code
    var diagnosticCode2 = "17";
    var request2 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode2, claimId1);
    masProcessingService.processIncomingClaim(request2);
    var claimEntity2 = verifyClaimPersisted(request2);
    contentions = claimEntity2.getContentions();
    assertEquals(2, contentions.size());

    // new claim
    var claimId2 = "321";
    var request3 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId2);
    masProcessingService.processIncomingClaim(request3);
    verifyClaimPersisted(request3);
  }

  @Test
  public void testNotInScope() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "71";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    var response1 = masProcessingService.processIncomingClaim(request1);
    // wrong diagnostic code
    assertEquals("Claim with collection Id 123 is out of scope.", response1);

    var request2 = MasTestData.getMasAutomatedClaimPayload(collectionId1, "7101", claimId1);
    request2.getClaimDetail().getConditions().setDisabilityActionType("OTHER");
    var response2 = masProcessingService.processIncomingClaim(request2);
    // wrong disability action
    assertEquals("Claim with collection Id 123 is out of scope.", response2);
  }

  @Test
  public void testInScopeButNotPresumptive() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "7101";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    request1.getClaimDetail().getConditions().setDisabilityActionType("NEW");
    var response = masProcessingService.processIncomingClaim(request1);
    assertEquals("Claim with collection Id 123 is not presumptive.", response);
  }

  @Test
  public void testInScopeAndPresumptiveButMissingAnchors() {
    var collectionId1 = 123;
    var claimId1 = "123";
    var diagnosticCode1 = "7101";
    var request1 =
        MasTestData.getMasAutomatedClaimPayload(collectionId1, diagnosticCode1, claimId1);
    request1.getClaimDetail().getConditions().setDisabilityActionType("NEW");
    request1 = request1.toBuilder().veteranFlashIds(List.of("123", "266")).build();
    var response = masProcessingService.processIncomingClaim(request1);
    assertEquals("Claim with collection Id 123 is missing an anchor.", response);
  }

  private ClaimEntity verifyClaimPersisted(MasAutomatedClaimPayload request) {
    var claim =
        claimRepository
            .findByClaimSubmissionIdAndIdType(
                request.getClaimId().toString(), "va.gov-Form526Submission")
            .orElseThrow();
    assertEquals(request.getCollectionId().toString(), claim.getCollectionId());
    assertEquals(request.getVeteranIcn(), claim.getVeteran().getIcn());
    return claim;
  }
}
