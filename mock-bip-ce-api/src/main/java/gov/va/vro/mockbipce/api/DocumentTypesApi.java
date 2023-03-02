package gov.va.vro.mockbipce.api;

import gov.va.vro.model.bipevidence.response.VefsErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
@Tag(name = "Document Types", description = "Document Types end-points")
@RequestMapping("/")
public interface DocumentTypesApi {
  /**
   * GET /documentTypes: Retrieve possible document types.
   *
   * <p>The return object is not implemented for Mock. We are only testing connectivity.
   */
  @Operation(
      operationId = "getDocumentTypes",
      summary = "Retrieves document types and alternative document types.",
      tags = {"Document Types"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "document types and alternative document types are returned",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                """
                Server was unable to understand the request.
                This may come back as an empty response if the json is
                malformed or not understood by the server.
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description =
                """
                JWT contains claims which indicate the consumer
                is not authorized to access the resource.
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description =
                """
                JWT contains claims which indicate the consumer
                is not authorized to access the resource
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Resource Not Found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description =
                """
                There was an error encountered processing the Request.
                Response will contain a \\"messages\\" element that will
                provide further information on the error. Please retry.
                If problem persists, please contact support with a copy of the Response.
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            })
      },
      security = {@SecurityRequirement(name = "bearer-key")})
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/documentTypes",
      produces = {"application/json"})
  ResponseEntity<String> getDocumentTypes();
}
