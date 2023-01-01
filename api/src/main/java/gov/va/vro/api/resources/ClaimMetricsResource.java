package gov.va.vro.api.resources;

import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.model.MetricsProcessingException;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface ClaimMetricsResource {
  @Operation(
      summary = "Retrieves claim specific data.",
      description = "Gets claim info for a specific claim. ")
  @GetMapping(value = "/claim-info/{claimSubmissionId}")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Successful"),
          @ApiResponse(
              responseCode = "400",
              description = "Bad Request",
              content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized",
              content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(
              responseCode = "500",
              description = "Claim Metrics Server Error",
              content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-info-claim-id")
  @Tag(name = "Claim Metrics")
  @ResponseBody
  ResponseEntity<gov.va.vro.model.claimmetrics.response.ClaimInfoResponse> claimInfoForClaimId(@PathVariable String claimSubmissionId)
      throws ClaimProcessingException;

  @Operation(
      summary = "Retrieves claim specific metrics for all claims.",
      description = "Retrieves claim specific metrics for all claims page by page.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Claim Metrics Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-info")
  @Tag(name = "Claim Metrics")
  @RequestMapping(value = "/claim-info", method = RequestMethod.GET)
  @Validated
  @ResponseBody
  ResponseEntity<List<ClaimInfoResponse>> claimInfoForAll(
      @RequestParam(name = "page", required = false, defaultValue = "0")
          @Min(value = 0, message = "invalid page number")
          @Valid
          Integer page,
      @RequestParam(name = "size", required = false, defaultValue = "10")
          @Min(value = 1, message = "invalid size")
          @Valid
          Integer size,
      @RequestParam(name = "icn", required = false) String icn);
}
