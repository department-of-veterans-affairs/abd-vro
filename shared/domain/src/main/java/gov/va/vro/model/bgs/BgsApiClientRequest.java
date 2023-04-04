package gov.va.vro.model.bgs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BgsApiClientRequest {

  final String vbmsClaimId;
  final String veteranParticipantId;

  /**
   * Veteran-level notes must be submitted separately from claim-level notes and submitted
   * individually (one at a time).
   */
  public String veteranNote;

  /** Multiple claim notes can be submitted */
  public List<String> claimNotes = new ArrayList<>();
}
