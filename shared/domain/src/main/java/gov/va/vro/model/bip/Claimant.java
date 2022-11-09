package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Claimant component of a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@Component
@RequiredArgsConstructor
@Data
public class Claimant {
  private long participantId;
}
