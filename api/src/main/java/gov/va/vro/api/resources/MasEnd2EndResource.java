package gov.va.vro.api.resources;

import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping(value = "/vx", produces = "application/json")
@Tag(name = "Test MAS Integration")
public interface MasEnd2EndResource {

  @Operation(summary = "Test MAS Claim Request")
  @PostMapping(value = "/testAutomatedClaim", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<MasResponse> processAutomatedClaim(
      @Parameter(
              description = "Request a MAS Automated Claim",
              required = true,
              schema = @Schema(implementation = MasAutomatedClaimPayload.class))
          @Valid
          @RequestBody
          MasAutomatedClaimPayload payload);
}
