package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ProviderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * Generated using Open API Specification of BIP Claims API (bipclaim_3.1.1.json) amd
 * openapitools/openapi-generator-cli. All the operations not being used by VRO removed.
 * All the tags are also removed.
 */
@Validated
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
   *     Response will contain a \&quot;messages\&quot; element that will provide further
   *     information on the error. Please retry. If problem persists, please contact support with a
   *     copy of the Response. (status code 500)
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
                a  \"messages\" element that will provide further information on the error.
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
  @RequestMapping(
      method = RequestMethod.GET,
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
}
