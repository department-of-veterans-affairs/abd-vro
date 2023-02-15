package gov.va.vro;

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
   * @param id ID
   * @param veteran veteran
   * @param referenceId also known as claimSubmissionId
   * @return return
   */
  public static ClaimEntity createClaim(String id, VeteranEntity veteran, String referenceId) {
    ClaimEntity claim = new ClaimEntity();
    claim.setVbmsId(id);
    claim.setVeteran(veteran);
    ClaimSubmissionEntity claimSubmission = new ClaimSubmissionEntity();
    claimSubmission.setReferenceId(referenceId);
    claimSubmission.setIdType(Claim.DEFAULT_ID_TYPE);
    claim.addClaimSubmission(claimSubmission);
    return claim;
  }
}
