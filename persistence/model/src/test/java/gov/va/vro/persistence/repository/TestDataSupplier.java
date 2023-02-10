package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.VeteranEntity;

import java.util.Date;

public class TestDataSupplier {
  /***
   * <p>Create Veteran.</p>
   *
   * @param icn veteran icn
   *
   * @param participantId participant id
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
   * <p>Create Claim.</p>
   *
   * @param id
   *
   *
   * @param veteran
   *
   * @return
   */
  public static ClaimEntity createClaim(String id, VeteranEntity veteran) {
    ClaimEntity claim = new ClaimEntity();
    claim.setVbmsId(id);
    claim.setVeteran(veteran);
    return claim;
  }

  public static ClaimSubmissionEntity createClaimSubmission(
      ClaimEntity claim, String reference_id, String id_type) {
    ClaimSubmissionEntity claimSubmission = new ClaimSubmissionEntity();
    claimSubmission.setClaim(claim);
    claimSubmission.setReferenceId(reference_id);
    claimSubmission.setIdType(id_type);
    return claimSubmission;
  }
}
