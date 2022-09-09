package gov.va.vro.api.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @ResponseStatus(HttpStatus.CREATED)
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
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "evidence-pdf")
  @Tag(name = "Pdf Generation")
  ResponseEntity<Object> fetchPdf(@PathVariable String claimSubmissionId)
      throws RequestValidationException, ClaimProcessingException;

  @Operation(
      summary = "Full health data assessment",
      description = "Provides full health data assessment for a claim")
  @PostMapping("/full-health-data-assessment")
  @ResponseStatus(HttpStatus.CREATED)
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
  @GetMapping("/fetch-claims")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "fetch-claims")
  ResponseEntity<FetchClaimsResponse> fetchClaims()
      throws RequestValidationException, ClaimProcessingException;
}
