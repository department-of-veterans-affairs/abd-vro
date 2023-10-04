package gov.va.vro.bip.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * List of update contentions for a claim.
 *
 * @author greene @Date 9/24/23
 */
@Getter
@Setter
@Builder
public class UpdateContentionModel {
  public long claimId;
  public UpdateContentionReq updateContentions;
}
