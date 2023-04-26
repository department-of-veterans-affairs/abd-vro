package gov.va.vro.mockbipce.api;

import gov.va.vro.model.rrd.bipevidence.response.UploadResponse;
import gov.va.vro.model.rrd.bipevidence.response.VefsErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
@RequestMapping("/")
public interface ReceivedApi {
  /**
   * GET /received-files/{fileNumber}: Returns the evidence file uploaded by POST /files. Provides
   * access to the file for testing purposes.
   *
   * @param fileNumber The file number of the veteran whose evidence files has been uploaded.
   * @return The file
   */
  @Operation(
      operationId = "download",
      summary = "Download the file for the veteran identified by the file number.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                """
                Response containing the file UUID, the owner, and the calculated MD5 Hash.
                As well as conversion information if the document has been converted.
                """,
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UploadResponse.class))
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
            responseCode = "405",
            description = "Method Not Allowed",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            }),
        @ApiResponse(
            responseCode = "415",
            description =
                "Unsupported Media Type. This is common when uploading an unacceptable file type.",
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
            }),
        @ApiResponse(
            responseCode = "501",
            description = "This request is not yet implemented.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = VefsErrorResponse.class))
            })
      },
      security = {@SecurityRequirement(name = "bearer-key")})
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/received-files/{fileNumber}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
  ResponseEntity<byte[]> download(
      @Parameter(name = "fileNumber", description = "The file number of the Veteran") @PathVariable
          String fileNumber);

  /** DELETE /received-files/{fileNumber}: Removes the document for the folder. */
  @Operation(
      operationId = "remove",
      summary = "Removes the document for the .",
      description = "Removes the document for the document.",
      responses = {@ApiResponse(responseCode = "204", description = "Document removed.")})
  @RequestMapping(method = RequestMethod.DELETE, value = "/received-files/{fileNumber}")
  ResponseEntity<Void> remove(
      @Parameter(
              name = "fileNumber",
              description = "The file number of the Veteran",
              required = true,
              in = ParameterIn.PATH)
          @PathVariable("fileNumber")
          String fileNumber);
}
