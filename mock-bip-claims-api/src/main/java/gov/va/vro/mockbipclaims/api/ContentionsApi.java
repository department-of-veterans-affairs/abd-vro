package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.bip.response.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateContentionsResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Generated using Open API Specification of BIP Claims API (bipclaim_3.1.1.json) amd
 * openapitools/openapi-generator-cli. All the operations not being used by VRO removed. All the
 * tags are also removed.
 */
@Validated
@Tag(name = "Contentions")
@RequestMapping("/")
public interface ContentionsApi {
  /**
   * GET /claims/{claimId}/contentions : List summaries for contentions associated with a given
   * claim.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @return A list of contention summary objects (status code 200) or No contentions found for the
   *     claim (status code 204) or The authentication mechanism failed and hence access is
   *     forbidden. (status code 401) or Could not derive claim from request path (status code 404)
   *     or There was an error encountered processing the Request. Response will contain a
   *     \&quot;messages\&quot; element that will provide further information on the error. Please
   *     retry. If problem persists, please contact support with a copy of the Response. (status
   *     code 500)
   */
  @Operation(
      operationId = "getContentionsForClaim",
      summary = "List summaries for contentions associated with a given claim.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "A list of contention summary objects",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ContentionSummariesResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ContentionSummariesResponse.class))
            }),
        @ApiResponse(responseCode = "204", description = "No contentions found for the claim"),
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
                a "messages" element that will provide further information on the error.  Please
                retry.  If problem persists, please contact support with a copy of the Response.
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
      value = "/claims/{claimId}/contentions",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<ContentionSummariesResponse> getContentionsForClaim(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId);

  /**
   * PUT /claims/{claimId}/contentions : Updates one or more contentions.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @param updateContentionsRequest (required)
   * @return Response indicates successful updates. May have messages with any warnings. (status
   *     code 200) or There was an error encountered processing the Request. Response will contain a
   *     "messages" element that will provide further information on the error. This request
   *     shouldn't be retried until corrected. (status code 400) or The authentication mechanism
   *     failed and hence access is forbidden. (status code 401) or There was an error encountered
   *     processing the Request. Response will contain a "messages"; element that will provide
   *     further information on the error. Please retry. If problem persists, please contact support
   *     with a copy of the Response. (status code 500) or Resource not implemented (status code
   *     501)
   */
  @Operation(
      operationId = "updateContentions",
      summary = "Updates one or more contentions",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Response indicates successful updates. May have messages with any warnings.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UpdateContentionsResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = UpdateContentionsResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                """
                There was an error encountered processing the Request.  Response will contain
                a "messages" element that will provide further information on the error.  This
                request shouldn't be retried until corrected.
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProviderResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProviderResponse.class))
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
            }),
        @ApiResponse(
            responseCode = "501",
            description = "Resource not implemented",
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
      method = RequestMethod.PUT,
      value = "/claims/{claimId}/contentions",
      produces = {"application/json", "application/problem+json"},
      consumes = {"application/json"})
  ResponseEntity<UpdateContentionsResponse> updateContentions(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId,
      @Parameter(name = "UpdateContentionsRequest", required = true) @Valid @RequestBody
          UpdateContentionsRequest updateContentionsRequest);
}
