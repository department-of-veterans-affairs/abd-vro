package gov.va.vro.api.rrd.resources;

import gov.va.vro.api.rrd.responses.BipVerificationResponse;
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

@RequestMapping(value = "/v2", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface VerificationResource {
  @Operation(
      summary = "Executes BIP connectivity verification queries.",
      description =
          """
          This end-point calls un-consequential GET end-points in BIP Claims and
          BIP Claim Evidence APIs to test connectivity.
          """)
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
  @GetMapping("/bip-verification-test")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "bip-verification-test")
  @Tag(name = "Verification Test")
  ResponseEntity<BipVerificationResponse> bipVerificationTest();
}
