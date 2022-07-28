package gov.va.vro.api.demo.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.demo.requests.AssessHealthDataRequest;
import gov.va.vro.api.demo.responses.AssessHealthDataResponse;
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
@Tag(
    name = "ABD-VRO Demo API",
    description = "Automated Benefit Delivery In-Progress Implementations")
@SecurityRequirement(name = "bearer-jwt")
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
}
