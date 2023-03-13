package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
import gov.va.vro.mockbipclaims.model.bip.request.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.bip.response.UpdateClaimLifecycleStatusResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
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
 * openapitools/openapi-generator-cli. All the operations not being used by VRO removed All the tags
 * are also removed.
 */
@Validated
@Tag(name = "Lifecycle Statuses")
@RequestMapping("/")
public interface LifecycleStatusesApi {
  /**
   * PUT /claims/{claimId}/lifecycle_status : Update the lifecycle status of an existing claim
   * Update the lifecycle status of an existing claim.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @param updateClaimLifecycleStatusRequest (required)
   * @return Updated (status code 200) or There was an error encountered processing the Request.
   *     Response will contain a \&quot;messages\&quot; element that will provide further
   *     information on the error. This request shouldn&#39;t be retried until corrected. (status
   *     code 400) or The authentication mechanism failed and hence access is forbidden. (status
   *     code 401) or Could not derive claim from request path (status code 404) or There was an
   *     error encountered processing the Request. Response will contain a \&quot;messages\&quot;
   *     element that will provide further information on the error. Please retry. If problem
   *     persists, please contact support with a copy of the Response. (status code 500) or Resource
   *     not implemented (status code 501) Claim Update documentation
   * @see <a
   *     href="https://github.ec.va.gov/EPMO/bip-vetservices-claims/blob/development/bip-vetservices-claims-docs/claim-updates.md">Update
   *     the lifecycle status of an existing claim Documentation</a>
   */
  @Operation(
      operationId = "updateClaimLifecycleStatus",
      summary = "Update the lifecycle status of an existing claim",
      description = "Update the lifecycle status of an existing claim",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Updated",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UpdateClaimLifecycleStatusResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = UpdateClaimLifecycleStatusResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                """
                There was an error encountered processing the Request.  Response will contain
                a "messages" element that will provide further information on the error.
                This request shouldn't be retried until corrected.
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
      security = {@SecurityRequirement(name = "bearerAuth")},
      externalDocs =
          @ExternalDocumentation(
              description = "Claim Update documentation",
              url =
                  "https://github.ec.va.gov/EPMO/bip-vetservices-claims/blob/development/bip-vetservices-claims-docs/claim-updates.md"))
  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/claims/{claimId}/lifecycle_status",
      produces = {"application/json", "application/problem+json"},
      consumes = {"application/json"})
  ResponseEntity<UpdateClaimLifecycleStatusResponse> updateClaimLifecycleStatus(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId,
      @Parameter(name = "UpdateClaimLifecycleStatusRequest", required = true) @Valid @RequestBody
          UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest);
}
