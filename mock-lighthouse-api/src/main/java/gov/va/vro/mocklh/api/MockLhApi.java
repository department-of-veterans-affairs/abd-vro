package gov.va.vro.mocklh.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface MockLhApi {
  /** POST /token : Get token based on specifications. */
  @Operation(
      operationId = "getToken",
      summary = "Retrieves JWT.",
      description =
          """
          For actual ICN's this returns the token from the LH server. Otherwise this returns
          a mock JWT.
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The JWT",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "Internal error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/token",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<String> getToken(@RequestParam MultiValueMap<String, String> requestBody);
}
