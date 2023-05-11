package gov.va.vro;

import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.service.spi.model.Claim;

import java.util.Date;

public class TestDataSupplier {
  /***
   * <p>Summary.</p>
   *
   * @param icn veteran ICN
   *
   * @param participantId participant ID
   *
   * @return return value
   */
  public static VeteranEntity createVeteran(String icn, String participantId, Date icnTimestamp) {
    VeteranEntity veteran = new VeteranEntity();
    veteran.setIcn(icn);
    veteran.setParticipantId(participantId);
    veteran.setIcnTimestamp(icnTimestamp);
    return veteran;
  }

  /***
   * <p>Summary.</p>
   *
   * @param benefitClaimId ID , this may be null in v1 calls
   * @param veteran veteran
   * @param referenceId also known as claimSubmissionId
   * @return return
   */
  public static ClaimEntity createClaim(
      String benefitClaimId, VeteranEntity veteran, String referenceId) {
    ClaimEntity claim = new ClaimEntity();
    claim.setVbmsId(benefitClaimId);
    claim.setVeteran(veteran);
    ClaimSubmissionEntity claimSubmission = new ClaimSubmissionEntity();
    claimSubmission.setReferenceId(referenceId);
    if (benefitClaimId == null) {
      claimSubmission.setIdType(Claim.V1_ID_TYPE);
    } else {
      claimSubmission.setIdType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
    }
    claim.addClaimSubmission(claimSubmission);
    return claim;
  }
}
