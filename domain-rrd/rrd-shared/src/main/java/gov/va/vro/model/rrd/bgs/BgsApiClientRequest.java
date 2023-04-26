package gov.va.vro.model.rrd.bgs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BgsApiClientRequest {

  // aka benefitClaimId
  final String vbmsClaimId;
  final String veteranParticipantId;

  /**
   * Veteran-level notes must be submitted separately from claim-level notes and submitted
   * individually (one at a time).
   */
  public String veteranNote;

  /** Multiple claim notes can be submitted */
  public List<String> claimNotes = new ArrayList<>();

  public boolean isConstraintSatisfied() {
    if (veteranNote == null) return vbmsClaimId != null && !claimNotes.isEmpty();
    else return veteranParticipantId != null && claimNotes.isEmpty();
  }
}
