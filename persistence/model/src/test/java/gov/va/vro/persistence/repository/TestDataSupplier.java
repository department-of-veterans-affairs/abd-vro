package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;

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
  public static VeteranEntity createVeteran(String icn, String participantId, String icnTimestamp) {
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
   * @param idType
   *
   * @param veteran
   *
   * @return
   */
  public static ClaimEntity createClaim(String id, String idType, VeteranEntity veteran) {
    ClaimEntity claim = new ClaimEntity();
    claim.setClaimSubmissionId(id);
    claim.setIdType(idType);
    claim.setVeteran(veteran);
    return claim;
  }
}
