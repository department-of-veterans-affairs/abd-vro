package gov.va.vro.api.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.responses.FetchClaimsResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface DevResource {

  @Operation(
      summary = "Gets all claims stored in vro database.",
      description =
          "Retrieves all claims from vro db and displays claimSubmissionId,"
              + " veteran ICN, and contention diagnostic codes.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Successful"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Fetch Claims Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @GetMapping("/fetch-claims")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "fetch-claims")
  @Tag(name = "Claim Metrics")
  ResponseEntity<FetchClaimsResponse> fetchClaims()
      throws RequestValidationException, ClaimProcessingException;
}
