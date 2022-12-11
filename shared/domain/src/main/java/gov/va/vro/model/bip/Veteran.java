package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A veteran component in a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Data
public class Veteran {
  private long participantId;
  private String firstName;
  private String lastName;
}
