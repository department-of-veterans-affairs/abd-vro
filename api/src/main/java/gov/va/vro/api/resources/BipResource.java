package gov.va.vro.api.resources;

import gov.va.vro.api.model.bip.BipUpdateClaimPayload;
import gov.va.vro.api.responses.BipClaimResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

/**
 * BIP API
 *
 * @author warren @Date 11/7/22
 */
@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface BipResource {

  @Operation(
      summary = "BIP Claim Update to RFD",
      description = "Updates a claim status to be Ready for Fast Decision.")
  @PostMapping("/updateClaim")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-update-claim")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipClaimResponse> updateClaim(
      @Parameter(
              description = "Request to update the status of a claim",
              required = true,
              schema = @Schema(implementation = BipUpdateClaimPayload.class))
          @Valid
          @RequestBody
          BipUpdateClaimPayload request);
}
