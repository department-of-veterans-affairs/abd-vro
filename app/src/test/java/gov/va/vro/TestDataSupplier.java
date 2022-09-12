package gov.va.vro;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;

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
  public static VeteranEntity createVeteran(String icn, String participantId) {
    VeteranEntity veteran = new VeteranEntity();
    veteran.setIcn(icn);
    veteran.setParticipantId(participantId);
    return veteran;
  }

  /***
   * <p>Summary.</p>
   *
   * @param id ID
   * @param idType ID type
   * @param veteran veteran
   * @return return
   */
  public static ClaimEntity createClaim(String id, String idType, VeteranEntity veteran) {
    ClaimEntity claim = new ClaimEntity();
    claim.setClaimSubmissionId(id);
    claim.setIdType(idType);
    claim.setVeteran(veteran);
    return claim;
  }
}
