package gov.va.vro.api.responses;

import gov.va.vro.model.bip.ClaimContention;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Bip claim contention response.
 *
 * @author warren @Date 11/16/22
 */
@Builder
@Getter
@Schema(name = "BIPClaimContentionsResponse", description = "A list of contentions for a claim.")
public class BipClaimContentionsResponse {
  private long claimId;
  private List<ClaimContention> contentions;
  private String message;
}
