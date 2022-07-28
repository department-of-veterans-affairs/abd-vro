package gov.va.vro.api.demo.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.demo.requests.AssessHealthDataRequest;
import gov.va.vro.api.demo.requests.GeneratePdfRequest;
import gov.va.vro.api.demo.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.demo.responses.AssessHealthDataResponse;
import gov.va.vro.api.demo.responses.GeneratePdfResponse;
import gov.va.vro.api.demo.responses.HealthDataAssessmentResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RequestMapping(value = "/v1/demo", produces = "application/json")
@Tag(name = "ABD-VRO API", description = "Automated Benefit Delivery Implementation")
// @SecurityRequirement(name = "bearer-jwt")
@SecurityRequirement(name = "X-API-Key")
@Timed
public interface DemoResource {
  @Operation(summary = "Demo assess_health_data", description = "Submit health data for assessment")
  @PostMapping("/assess_health_data")
  @ResponseStatus(HttpStatus.CREATED)
  @Timed(value = "example.assess_health_data")
  ResponseEntity<AssessHealthDataResponse> assess_health_data(
      @Parameter(
              description = "metadata for assess_health_data",
              required = true,
              schema = @Schema(implementation = AssessHealthDataRequest.class))
          @Valid
          @RequestBody
          AssessHealthDataRequest request)
      throws RequestValidationException;

  @Operation(
      summary = "Health data assesment",
      description = "Provides health data assesment for the claim")
  @PostMapping("/health-data-assessment")
  @ResponseStatus(HttpStatus.CREATED)
  @Timed(value = "health-data-assessment")
  ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(
      @Parameter(
              description = "Claim for which health data assessment requested",
              required = true,
              schema = @Schema(implementation = HealthDataAssessmentRequest.class))
          @Valid
          @RequestBody
          HealthDataAssessmentRequest claim)
      throws RequestValidationException;

  @Operation(summary = "Demo generate_pdf", description = "Submit data for pdf generation")
  @PostMapping("/generate_pdf")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.generate_pdf")
  ResponseEntity<GeneratePdfResponse> generate_pdf(
      @Parameter(
              description = "metadata for generate_pdf",
              required = true,
              schema = @Schema(implementation = GeneratePdfRequest.class))
          @Valid
          @RequestBody
          GeneratePdfRequest request)
      throws RequestValidationException;

  @Operation(summary = "Demo fetch_pdf", description = "Submit data for pdf fetching")
  @PostMapping("/fetch_pdf")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.fetch_pdf")
  ResponseEntity<Object> fetch_pdf(
      @Parameter(
              description = "metadata for fetch_pdf",
              required = true,
              schema = @Schema(implementation = GeneratePdfRequest.class))
          @Valid
          @RequestBody
          GeneratePdfRequest request)
      throws RequestValidationException;
}
