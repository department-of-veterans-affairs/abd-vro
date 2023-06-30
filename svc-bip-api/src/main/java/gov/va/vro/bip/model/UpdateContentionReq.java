package gov.va.vro.bip.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request to update contentions for a claim.
 *
 * @author warren @Date 11/14/22
 */
@Getter
@Setter
@Builder
public class UpdateContentionReq {
  long claimId;
  private List<UpdateContention> updateContentions;
}
