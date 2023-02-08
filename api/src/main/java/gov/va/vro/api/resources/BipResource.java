package gov.va.vro.api.resources;

import gov.va.vro.api.responses.BipClaimContentionsResponse;
import gov.va.vro.api.responses.BipClaimResponse;
import gov.va.vro.api.responses.BipClaimStatusResponse;
import gov.va.vro.api.responses.BipContentionUpdateResponse;
import gov.va.vro.api.responses.BipFileUploadResponse;
import gov.va.vro.model.bip.BipUpdateClaimContentionPayload;
import gov.va.vro.model.bip.BipUpdateClaimPayload;
import gov.va.vro.model.bipevidence.BipFileProviderData;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * BIP API.
 *
 * @author warren @Date 11/7/22
 */
@RequestMapping(value = "/v1", produces = "application/json")
@SecurityRequirement(name = "X-API-Key")
@SecurityScheme(name = "X-API-Key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Timed
public interface BipResource {

  @Operation(
      summary = "BIP Claim Update to RFD",
      description = "Updates a claim status to be Ready For Decision.")
  @PutMapping("/claim/setrfd")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-update-claim")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipClaimStatusResponse> setClaimRfd(
      @Parameter(
              description = "Request to update the status of a claim",
              required = true,
              schema = @Schema(implementation = BipUpdateClaimPayload.class))
          @Valid
          @RequestBody
          BipUpdateClaimPayload request);

  @Operation(summary = "Get claim information.", description = "Request claim information.")
  @GetMapping("/claim/info/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-claim-info")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipClaimResponse> getClaim(
      @Parameter(
              description = "a claim ID for claim information",
              required = true,
              schema = @Schema(implementation = String.class))
          @Valid
          @PathVariable(value = "id")
          String id);

  @Operation(
      summary = "Get claim contentions",
      description = "Request a claim contention information.")
  @GetMapping("/claim/contention/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error.",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-claim-contention")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipClaimContentionsResponse> getContentions(
      @Parameter(
              description = "Request claim contentions",
              required = true,
              schema = @Schema(implementation = String.class))
          @Valid
          @PathVariable(value = "id")
          String id);

  @Operation(
      summary = "Update a claim contention",
      description = "Request to update a claim contention.")
  @PutMapping("/claim/contention")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-claim-contention-updt")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipContentionUpdateResponse> updateContentions(
      @Parameter(
              description = "Request to update a contention for a claim.",
              required = true,
              schema = @Schema(implementation = BipUpdateClaimContentionPayload.class))
          @Valid
          @RequestBody
          BipUpdateClaimContentionPayload payload);

  @Operation(summary = "Upload evidence file", description = "Upload evidence PDF file.")
  @PostMapping(
      value = "/evidence/files",
      consumes = {"multipart/form-data"})
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Data Access Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  @Timed(value = "bip-evidence-fileupload")
  @Tag(name = "BIP Integration")
  ResponseEntity<BipFileUploadResponse> fileUpload(
      @Parameter(description = "file ID", required = true, schema = @Schema(type = "string"))
          @Valid
          @RequestParam(value = "fileId")
          String fileId,
      @Parameter(description = "file ID type", required = true, schema = @Schema(type = "string"))
          @Valid
          @RequestParam(value = "fileIdType")
          String fileIdType,
      @RequestPart(value = "providerData")
          @Parameter(
              description = "provider data",
              required = true,
              schema = @Schema(type = "string", format = "binary"))
          final BipFileProviderData providerData,
      @RequestPart(value = "file") @Parameter(description = "file", required = true)
          final MultipartFile file);
}
