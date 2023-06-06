package gov.va.vro.bip.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Claimant component of a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Data
public class Claimant {
  private long participantId;
}
