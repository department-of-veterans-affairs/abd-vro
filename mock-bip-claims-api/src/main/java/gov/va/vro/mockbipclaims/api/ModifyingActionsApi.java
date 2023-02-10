package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.store.ModifyingActionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/")
public interface ModifyingActionsApi {

  /**
   * GET /modifying-actions/{claimId}/lifecycle-status: Retrieves if lifecycle status of the
   * claim is updated.
   **/
  @Operation(
      operationId = "getLifecycleStatusUpdated",
      summary = "Retrieves if contentions are updated.",
      description = "Retrieves if contentions are updated.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "If update happened.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ModifyingActionsResponse.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/modifying-actions/{claimId}/lifecycle_status",
      produces = {"application/json"})
  ResponseEntity<ModifyingActionsResponse> getLifecycleStatusUpdated(
      @Parameter(
          name = "claimId",
          description = "The CorpDB BNFT_CLAIM_ID",
          required = true,
          in = ParameterIn.PATH)
      @PathVariable("claimId")
      Long claimId);

  /**
   * GET /modifying-actions/{claimId}/contentions: Retrieves if any of the contentions of the
   * claim is updated.
   **/
   @Operation(
      operationId = "getContentionsUpdated",
      summary = "Retrieves if contentions are updated.",
      description = "Retrieves if contentions are updated.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "If update happened.",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ModifyingActionsResponse.class))
              })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/modifying-actions/{claimId}/contentions",
      produces = {"application/json"})
  ResponseEntity<ModifyingActionsResponse> getContentionsUpdated(
      @Parameter(
          name = "claimId",
          description = "The CorpDB BNFT_CLAIM_ID",
          required = true,
          in = ParameterIn.PATH)
      @PathVariable("claimId")
      Long claimId);
}
