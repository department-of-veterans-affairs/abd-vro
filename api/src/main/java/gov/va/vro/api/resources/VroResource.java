package gov.va.vro.api.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.model.MetricsProcessingException;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.ClaimMetricsResponse;
import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface VroResource {
  @Operation(
      summary = "Health data assessment",
      description = "Provides health data assessment for the claim")
  @PostMapping("/health-data-assessment")
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
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value = "{claimSubmissionId: 1234, message = No evidence found}")))
      })
  @Timed(value = "health-data-assessment")
  @Tag(name = "Health Assessment")
  ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(
      @Parameter(
              description = "Claim for which health data assessment requested",
              required = true,
              schema = @Schema(implementation = HealthDataAssessmentRequest.class))
          @Valid
          @RequestBody
          HealthDataAssessmentRequest claim)
      throws RequestValidationException, ClaimProcessingException;

  @Operation(
      summary = "Evidence pdf generation launch",
      description =
          "Posts health data for a specific patient and diagnostic code to "
              + "launch evidence pdf generation")
  @PostMapping("/evidence-pdf")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Successful Request"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "PDF Generator Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "evidence-pdf")
  @Tag(name = "Pdf Generation")
  ResponseEntity<GeneratePdfResponse> generatePdf(
      @Parameter(
              description = "metadata for generatePdf",
              required = true,
              schema = @Schema(implementation = GeneratePdfRequest.class))
          @Valid
          @RequestBody
          GeneratePdfRequest request)
      throws RequestValidationException, ClaimProcessingException;

  @Operation(
      summary = "Generated evidence pdf download",
      description =
          "Downloads the evidence pdf previously generated by the posted"
              + " patient health data and diagnostic code")
  @GetMapping("/evidence-pdf/{claimSubmissionId}")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "PDF Generator Server Error",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "PDF generation for specified claimSubmissionID not requested",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "evidence-pdf")
  @Tag(name = "Pdf Generation")
  ResponseEntity<Object> fetchPdf(@PathVariable String claimSubmissionId)
      throws RequestValidationException, ClaimProcessingException;

  @Operation(
      summary = "Full health data assessment",
      description = "Provides full health data assessment for a claim")
  @PostMapping("/full-health-data-assessment")
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
            description = "Claim Processing Server Error",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "No evidence found",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value = "{claimSubmissionId: 1234, message = No evidence found}")))
      })
  @Timed(value = "full-health-data-assessment")
  @Tag(name = "Full Health Assessment")
  ResponseEntity<FullHealthDataAssessmentResponse> postFullHealthAssessment(
      @Parameter(
              description = "Claim for which health data assessment requested",
              required = true,
              schema = @Schema(implementation = HealthDataAssessmentRequest.class))
          @Valid
          @RequestBody
          HealthDataAssessmentRequest claim)
      throws RequestValidationException, ClaimProcessingException;

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

  @Operation(
      summary = "Retrieves metrics on claims stored in abd-vro database",
      description = "Returns number of claims in abd-vro databse")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Successful"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Claim Metrics Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @GetMapping("/claim-metrics")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "claim-metrics")
  @Tag(name = "Claim Metrics")
  ResponseEntity<ClaimMetricsResponse> claimMetrics()
      throws RequestValidationException, ClaimProcessingException, MetricsProcessingException;
}
