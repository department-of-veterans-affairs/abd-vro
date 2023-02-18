package gov.va.vro.api.resources;

import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RequestMapping(value = "/v2", produces = "application/json")
@SecurityRequirement(name = "Bearer Authentication")
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER)
@Timed
public interface HealthAssessmentResource {

  @Operation(
      summary = "Provides health data assessment",
      description =
          "This endpoint has the same functionality as v1/full-health-data-assessment, although "
              + "it will only get a health assessment for claims submitted to v2 endpoints. To see v1 claims, use the "
              + "v1/full-health-assessment endpoint.")
  @PostMapping("/health-data-assessment")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Successful Request"),
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
            description = "Claim Processing Server Error",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
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
  ResponseEntity<FullHealthDataAssessmentResponse> postHealthAssessment(
      @Parameter(
              description = "Claim for which health data assessment requested",
              required = true,
              schema = @Schema(implementation = HealthDataAssessmentRequest.class))
          @Valid
          @RequestBody
          HealthDataAssessmentRequest claim)
      throws MethodArgumentNotValidException, ClaimProcessingException;
}
