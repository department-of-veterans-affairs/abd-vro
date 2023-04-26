package gov.va.vro.mockbipce.api;

import gov.va.vro.model.rrd.bipevidence.response.UploadResponse;
import gov.va.vro.model.rrd.bipevidence.response.VefsErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Validated
@Tag(name = "files", description = "the files API")
@RequestMapping("/")
public interface FilesApi {
  /**
   * POST /files : Upload a file with associated provider data ### Upload a file. This endpoint when
   * given a file and associated data returns a UUID which can be used to retrieve back the latest
   * data provided. Information on how to properly creae a payload object for this endpoint is
   * available in the schema section.
   *
   * @param folderUri The Folder Identifier that the file will be associated to. The example
   *     provided is for identifying a veteran.&lt;br&gt;&lt;br&gt;**Header Format**:
   *     folder-type:identifier-type:ID&lt;br&gt;&lt;br&gt;**Valid Folder
   *     Types**:&lt;br&gt;&lt;br&gt;* VETERAN&lt;br&gt;&lt;br&gt;**Valid Identifier
   *     Types**:&lt;br&gt;&lt;br&gt;* FILENUMBER&lt;br&gt;* SSN&lt;br&gt;*
   *     PARTICIPANT_ID&lt;br&gt;* EDIPI (optional)
   * @param payload (optional)
   * @param file (optional)
   * @return Response containing the file UUID, the owner, and the calculated MD5 Hash. As well as
   *     conversion information if the document has been converted. (status code 200) or Server was
   *     unable to understand the request. This may come back as an empty response if the json is
   *     malformed or not understood by the server. (status code 400) or JWT contains claims which
   *     indicate the consumer is not authorized to access the resource. (status code 401) or JWT
   *     contains claims which indicate the consumer is not authorized to access the resource
   *     (status code 403) or Resource Not Found (status code 404) or Method Not Allowed (status
   *     code 405) or Unsupported Media Type. This is common when uploading an unacceptable file
   *     type. (status code 415) or There was an error encountered processing the Request. Response
   *     will contain a \&quot;messages\&quot; element that will provide further information on the
   *     error. Please retry. If problem persists, please contact support with a copy of the
   *     Response. (status code 500) or This request is not yet implemented. (status code 501)
   */
  @Operation(
      operationId = "upload",
      summary = "Upload a file with associated provider data",
      tags = {"File"},
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
      method = RequestMethod.POST,
      value = "/files",
      produces = {"application/json"},
      consumes = {"multipart/form-data"})
  ResponseEntity<UploadResponse> upload(
      @Parameter(
              name = "X-Folder-URI",
              description =
                  """
                  The Folder Identifier that the file will be associated to.
                  The example provided is for identifying a veteran.<br><br>**Header Format**:
                  folder-type:identifier-type:ID<br><br>**Valid Folder Types**:<br><br>*
                  VETERAN<br><br>**Valid  Identifier Types**:
                  <br><br>* FILENUMBER<br>* SSN<br>* PARTICIPANT_ID<br>* EDIPI
                  """)
          @RequestHeader(value = "X-Folder-URI", required = false)
          String folderUri,
      @Parameter(name = "payload", description = "")
          @Valid
          @RequestParam(value = "payload", required = false)
          String payload,
      @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false)
          MultipartFile file);
}
