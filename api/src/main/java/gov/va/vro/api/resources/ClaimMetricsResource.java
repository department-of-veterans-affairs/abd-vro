package gov.va.vro.api.resources;

import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import javax.validation.constraints.Min;

@RequestMapping(value = "/v2", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface ClaimMetricsResource {
  @Operation(
      summary = "Retrieves metrics on the previously processed claims",
      description = "This endpoint retrieves metrics on the previously processed claims.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal service error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @GetMapping("/claim-metrics")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-metrics")
  @Tag(name = "Claim Metrics")
  ResponseEntity<ClaimMetricsResponse> claimMetrics();

  @Operation(
      summary = "Retrieves claim specific information.",
      description =
          "Retrieves metrics for the specified claim. Defaults to claims from MAS ('v2'). Specify"
              + " the claim's endpoint version ('v1' or 'v2') for claims submitted to that"
              + "endpoint version.")
  @GetMapping(value = "/claim-info/{claimSubmissionId}")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal service error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-info-claim-id")
  @Tag(name = "Claim Metrics")
  @ResponseBody
  ResponseEntity<gov.va.vro.model.claimmetrics.response.ClaimInfoResponse> claimInfoForClaimId(
      @PathVariable String claimSubmissionId, @RequestParam(required = false) String claimVersion)
      throws ClaimProcessingException;

  @Operation(
      summary = "Retrieves claim specific metrics for all claims.",
      description = "This endpoint retrieves claim specific metrics for all claims page by page.")
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
            description = "Internal service error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-info")
  @Tag(name = "Claim Metrics")
  @RequestMapping(value = "/claim-info", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<List<ClaimInfoResponse>> claimInfoForAll(
      @RequestParam(name = "page", required = false, defaultValue = "0")
          @Min(value = 0, message = "invalid page number")
          Integer page,
      @RequestParam(name = "size", required = false, defaultValue = "10")
          @Min(value = 1, message = "invalid size")
          Integer size,
      @RequestParam(name = "icn", required = false) String icn);

  @Operation(
      summary = "Retrieves all exam order records",
      description = "This endpoint retrieves exam order metrics for all entries page by page.")
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
            description = "Internal service error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "exam-order-info")
  @Tag(name = "Claim Metrics")
  @RequestMapping(value = "/exam-order-info", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<List<ExamOrderInfoResponse>> allExamOrderInfo(
      @RequestParam(name = "page", required = false, defaultValue = "0")
          @Min(value = 0, message = "invalid page number")
          Integer page,
      @RequestParam(name = "size", required = false, defaultValue = "10")
          @Min(value = 1, message = "invalid size")
          Integer size);

  @Operation(
      summary = "Retrieves health evidence for a specific claimSubmissionId.",
      description = "Gets the health evidence for a specific claimSubmissionId(collectionId)")
  @GetMapping(value = "/health-evidence")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal service error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "health-evidence")
  @Tag(name = "Claim Metrics")
  @ResponseBody
  ResponseEntity<FullHealthDataAssessmentResponse> healthEvidence(
      HealthDataAssessmentRequest request) throws ClaimProcessingException;
}
