package gov.va.vro.mockbipclaims.api;

import gov.va.vro.mockbipclaims.model.ClaimDetailResponse;
import gov.va.vro.mockbipclaims.model.ClaimLifecycleStatusesResponse;
import gov.va.vro.mockbipclaims.model.ContentionSummariesResponse;
import gov.va.vro.mockbipclaims.model.ProviderResponse;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusRequest;
import gov.va.vro.mockbipclaims.model.UpdateClaimLifecycleStatusResponse;
import gov.va.vro.mockbipclaims.model.UpdateContentionsRequest;
import gov.va.vro.mockbipclaims.model.UpdateContentionsResponse;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Validated
@Tag(
    name = "claims",
    description = "A tag that organizes resources for contentions within a given claim.")
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
      tags = {"claims"},
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

  /**
   * GET /claims/{claimId}/lifecycle_status : Get the lifecycle status(es) of an existing claim.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @param includeHistory Whether or not to include historical lifecycle status updates. Default is
   *     false. (optional, default to false)
   * @return The suspense status list for an existing claim. (status code 200) or There was an error
   *     encountered processing the Request. Response will contain a \&quot;messages\&quot; element
   *     that will provide further information on the error. This request shouldn&#39;t be retried
   *     until corrected. (status code 400) or The authentication mechanism failed and hence access
   *     is forbidden. (status code 401) or Precondition Failed (status code 412) or There was an
   *     error encountered processing the Request. Response will contain a \&quot;messages\&quot;
   *     element that will provide further information on the error. Please retry. If problem
   *     persists, please contact support with a copy of the Response. (status code 500) or Resource
   *     not implemented (status code 501)
   */
  @Operation(
      operationId = "getClaimLifecycleStatuses",
      summary = "Get the lifecycle status(es) of an existing claim",
      tags = {"claims"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The suspense status list for an existing claim.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimLifecycleStatusesResponse.class)),
              @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ClaimLifecycleStatusesResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                """
                There was an error encountered processing the Request.  Response will contain
                a  \"messages\" element that will provide further information on the error.
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
            responseCode = "412",
            description = "Precondition Failed",
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
                a \"messages\" element that will provide further information on the error.  Please
                retry.  If problem persists, please contact support with a copy of the Response.
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
      method = RequestMethod.GET,
      value = "/claims/{claimId}/lifecycle_status",
      produces = {"application/json", "application/problem+json"})
  ResponseEntity<ClaimLifecycleStatusesResponse> getClaimLifecycleStatuses(
      @Parameter(
              name = "claimId",
              description = "The CorpDB BNFT_CLAIM_ID",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("claimId")
          Long claimId,
      @Parameter(
              name = "include_history",
              description =
                  """
                  Whether or not to include historical lifecycle status updates. Default is
                  false.
                  """,
              in = ParameterIn.QUERY)
          @Valid
          @RequestParam(value = "include_history", required = false, defaultValue = "false")
          Boolean includeHistory);

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
      tags = {"contentions"},
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
                a \"messages\" element that will provide further information on the error.  Please
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
      tags = {"claims"},
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
                a \"messages\" element that will provide further information on the error.
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
      @Parameter(name = "UpdateClaimLifecycleStatusRequest", description = "", required = true)
          @Valid
          @RequestBody
          UpdateClaimLifecycleStatusRequest updateClaimLifecycleStatusRequest);

  /**
   * PUT /claims/{claimId}/contentions : Updates one or more contentions.
   *
   * @param claimId The CorpDB BNFT_CLAIM_ID (required)
   * @param updateContentionsRequest (required)
   * @return Response indicates successful updates. May have messages with any warnings. (status
   *     code 200) or There was an error encountered processing the Request. Response will contain a
   *     \&quot;messages\&quot; element that will provide further information on the error. This
   *     request shouldn&#39;t be retried until corrected. (status code 400) or The authentication
   *     mechanism failed and hence access is forbidden. (status code 401) or There was an error
   *     encountered processing the Request. Response will contain a \&quot;messages\&quot; element
   *     that will provide further information on the error. Please retry. If problem persists,
   *     please contact support with a copy of the Response. (status code 500) or Resource not
   *     implemented (status code 501)
   */
  @Operation(
      operationId = "updateContentions",
      summary = "Updates one or more contentions",
      tags = {"contentions"},
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
                a \"messages\" element that will provide further information on the error.  This
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
      @Parameter(name = "UpdateContentionsRequest", description = "", required = true)
          @Valid
          @RequestBody
          UpdateContentionsRequest updateContentionsRequest);
}
