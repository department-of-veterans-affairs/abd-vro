package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(
    name = "MASClaimDetailsResponse",
    description = "Indicate that the request information has been collected")
public class MasResponse {

  private String id;
  private String message;
}
