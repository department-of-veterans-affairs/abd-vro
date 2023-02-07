package gov.va.vro.mockbipclaims.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/")
public interface ModifyingActionsApi {

  /** GET /modifying-actions: Provides modifying REST actions. */
  @Operation(
      operationId = "getModifyingActions",
      summary = "Provides modifying REST actions",
      description = "Provides modifying REST actions.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lst of actions.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String[].class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/modifying-actions",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<String[]> getModifyingActions();
}
