package gov.va.vro.api.resources;

import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
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

@RequestMapping(value = "/v2", produces = "application/json")
@SecurityRequirement(name = "Bearer Authentication")
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER)
@Timed
public interface MasResource {

  @Operation(
      summary = "MAS Claim Request",
      description =
          "Receives an initial request for a MAS claim and starts collecting the evidence")
  @PostMapping("/automatedClaim")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "Resource Not Found",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "422",
            description = "Bad Claim",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "mas-automated-claim")
  @Tag(name = "MAS Integration")
  ResponseEntity<MasResponse> automatedClaim(
      @Parameter(
              description = "Request a MAS Automated Claim",
              required = true,
              schema = @Schema(implementation = MasAutomatedClaimRequest.class))
          @Valid
          @RequestBody
          MasAutomatedClaimRequest request);

  @Operation(
      summary = "MAS Exam Ordering Status",
      description = "Request Ordering Status for an exam")
  @PostMapping("/examOrderingStatus")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "422",
            description = "Bad Claim",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "exam-ordering-status")
  @Tag(name = "MAS Integration")
  ResponseEntity<MasResponse> examOrderingStatus(
      @Parameter(
              description = "Request Exam ordering status",
              required = true,
              schema = @Schema(implementation = MasExamOrderStatusPayload.class))
          @Valid
          @RequestBody
          MasExamOrderStatusPayload payload);
}
