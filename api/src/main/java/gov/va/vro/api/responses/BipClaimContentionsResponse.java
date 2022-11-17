package gov.va.vro.api.responses;

import gov.va.vro.model.bip.ClaimContention;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** @author warren @Date 11/16/22 */
@Builder
@Getter
@Schema(name = "BIPClaimContentionsResponse", description = "A list of contentions for a claim.")
public class BipClaimContentionsResponse {
  List<ClaimContention> contentions;
}
