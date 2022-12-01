package gov.va.vro.model.bip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Request to update contentions for a claim.
 *
 * @author warren @Date 11/14/22
 */
@Getter
@RequiredArgsConstructor
public class UpdateContentionReq {
  private final List<ClaimContention> updateContentions;
}
