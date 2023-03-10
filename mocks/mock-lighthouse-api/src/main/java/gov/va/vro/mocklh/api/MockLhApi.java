package gov.va.vro.mocklh.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
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

  /** POST /token : Get an Observation bundle based on specifications. */
  @Operation(
      operationId = "getObservation",
      summary = "Retrieves Observation bundle.",
      description =
          """
          For actual ICN's this returns the Observation bundle from the LH server. Otherwise this
          returns a mock Observation bundle.
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The Observation bundle",
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
      method = RequestMethod.GET,
      value = "/Observation",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<String> getObservation(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
      @RequestParam MultiValueMap<String, String> queryParams);

  /** POST /token : Get a Condition bundle based on specifications. */
  @Operation(
      operationId = "getCondition",
      summary = "Retrieves Condition bundle.",
      description =
          """
          For actual ICN's this returns the Condition bundle from the LH server. Otherwise this
          returns a mock Observation bundle.
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The Condition bundle",
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
      method = RequestMethod.GET,
      value = "/Condition",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<String> getCondition(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
      @RequestParam MultiValueMap<String, String> queryParams);

  /** POST /token : Get an MedicationRequest bundle based on specifications. */
  @Operation(
      operationId = "getMedicationRequest",
      summary = "Retrieves MedicationRequest bundle.",
      description =
          """
          For actual ICN's this returns the MedicationRequest bundle 
          from the LH server. Otherwise this
          returns a mock MedicationRequest bundle.
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The MedicationRequest bundle",
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
      method = RequestMethod.GET,
      value = "/MedicationRequest",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<String> getMedicationRequest(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
      @RequestParam MultiValueMap<String, String> queryParams);
}
