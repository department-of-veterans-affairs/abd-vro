package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import gov.va.vro.mockbipclaims.model.bip.request.CloseClaimRequest;
import gov.va.vro.mockbipclaims.model.bip.request.PutTemporaryStationOfJurisdictionRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.bip.response.CloseClaimResponse;
import gov.va.vro.mockbipclaims.model.bip.response.PutTemporaryStationOfJurisdictionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Generated using Open API Specification of BIP Claims API (bipclaim_3.1.1.json) amd
 * openapitools/openapi-generator-cli. All the operations not being used by VRO removed. All the
 * tags are also removed.
 */
@Validated
@Tag(name = "Claims")
@RequestMapping("/")
public interface ClaimsApi {
  /**
   * GET /claims/{claimId} : Access claim summary information for a claim, using the claimId Get the
   * claim summary for a claim ID. This is the use case for getting claim information regardless of
   * veteran.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @return Details for the given claim id. (status code 200) or The authentication mechanism
   *     failed and hence access is forbidden. (status code 401) or Could not derive claim from
   *     request path (status code 404) or There was an error encountered processing the Request.
   *     Response will contain a "messages" element that will provide further information on the
   *     error. Please retry. If problem persists, please contact support with a copy of the
   *     Response. (status code 500)
   */
  @Operation(
      operationId = "getClaimById",
      summary = "Access claim summary information for a claim, using the claimId",
      description =
          """
              Get the claim summary for a claim ID. This is the use case for getting claim information
              regardless of veteran.
              """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Details for the given claim id.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimDetailResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ClaimDetailResponse.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication mechanism failed and hence access is forbidden.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProviderResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProviderResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Could not derive claim from request path",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProviderResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProviderResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description =
                """
                      There was an error encountered processing the Request.  Response will contain
                      a  "messages" element that will provide further information on the error.
                      Please retry.  If problem persists, please contact support with a copy of the
                      Response.
                      """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProviderResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProviderResponse.class))
            })
      },
      security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(
      value = "/claims/{claimId}",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<ClaimDetailResponse> getClaimById(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId);

  /**
   * PUT /claims/{claimId}/cancel : Cancel an existing claim using the claimId. Cancel a claim using
   * its claim ID. The claim must not already be closed or cancelled.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @return Claim Closed (status code 200) or The authentication mechanism failed and hence access
   *     is forbidden. (status code 401) or Could not derive claim from request path (status code
   *     404) or There was an error encountered processing the Request. Response will contain a
   *     "messages" element that will provide further information on the error. Please retry. If
   *     problem persists, please contact support with a copy of the Response. (status code 500)
   */
  @Operation(
      operationId = "cancelClaimById",
      summary = "Operation to cancel an existing claim",
      description =
          "Operation to cancel an existing claim. The claim must not already be closed or cancelled.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Claim Closed.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CloseClaimResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication mechanism failed and hence access is forbidden.",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Could not derive claim from request path",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "There was an error encountered processing the Request. Response will contain a \"messages\" element that will provide further information on the error. Please retry. If problem persists, please contact support with a copy of the Response.",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class)))
      },
      security = {@SecurityRequirement(name = "bearerAuth")})
  @PutMapping(
      value = "/claims/{claimId}/cancel",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<CloseClaimResponse> cancelClaimById(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId,
      @RequestBody CloseClaimRequest closeClaimRequest);

  /**
   * PUT /claims/{claimId}/temporaryStationOfJurisdiction : Set the temporary station of
   * jurisdiction on an existing claim using the claimId.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @return Updated (status code 200) or The authentication mechanism failed and hence access is
   *     forbidden. (status code 401) or Could not derive claim from request path (status code 404)
   *     or There was an error encountered processing the Request. Response will contain a
   *     "messages" element that will provide further information on the error. Please retry. If
   *     problem persists, please contact support with a copy of the Response. (status code 500)
   */
  @Operation(
      operationId = "setTemporaryStationOfJurisdictionById",
      summary = "Operation to set the temporary jurisdiction of an existing claim",
      description =
          "Set the temporary station of jurisdiction on an existing claim using the claimId. ",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Updated.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CloseClaimResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication mechanism failed and hence access is forbidden.",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Could not derive claim from request path",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description =
                "There was an error encountered processing the Request. Response will contain a \"messages\" element that will provide further information on the error. Please retry. If problem persists, please contact support with a copy of the Response.",
            content =
                @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProviderResponse.class)))
      },
      security = {@SecurityRequirement(name = "bearerAuth")})
  @PutMapping(
      value = "/claims/{claimId}/temporary_station_of_jurisdiction",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<PutTemporaryStationOfJurisdictionResponse> putTemporaryStationOfJurisdictionById(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId,
      @RequestBody
          PutTemporaryStationOfJurisdictionRequest putTemporaryStationOfJurisdictionRequest);
}
