package gov.va.vro;

import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;

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
   * @return return
   */
  public static ClaimEntity createClaim(String id, VeteranEntity veteran) {
    ClaimEntity claim = new ClaimEntity();
    claim.setVbmsId(id);
    claim.setVeteran(veteran);
    return claim;
  }
}
