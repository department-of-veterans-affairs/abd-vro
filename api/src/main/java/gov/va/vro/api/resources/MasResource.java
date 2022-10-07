package gov.va.vro.api.resources;

import gov.va.vro.api.requests.MasClaimDetailsRequest;
import gov.va.vro.api.responses.MasClaimResponse;
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

@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface MasResource {

  @Operation(
      summary = "MAS Claim Request",
      description =
          "Receives an initial request for a MAS claim and starts collecting the evidence")
  @PostMapping("/notifyVROAutomatedClaimDetails")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Successful Request"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "No evidence found",
            content = @Content(mediaType = "application/json"))
      })
  @Timed(value = "mas-claim-request")
  @Tag(name = "MAS Claim Request")
  ResponseEntity<MasClaimResponse> notifyAutomatedClaimDetails(
      @Parameter(
              description = "Request for a MAS Claim",
              required = true,
              schema = @Schema(implementation = MasClaimDetailsRequest.class))
          @Valid
          @RequestBody
          MasClaimDetailsRequest request);
}
