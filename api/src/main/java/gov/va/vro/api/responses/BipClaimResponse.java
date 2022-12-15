package gov.va.vro.api.responses;

import gov.va.vro.model.bip.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * A claim information object.
 *
 * @author warren @Date 11/16/22
 */
@Builder
@Getter
@Schema(name = "BIPClaimResponse", description = "Claim information")
public class BipClaimResponse { // TODO: refactor the code to include needed fields.
  private long claimId;
  private String message;
  private BipClaim claim;
}
