package gov.va.starter.example.api.subclaimsubmission.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.example.api.responses.PagedResponse;
import gov.va.starter.example.api.subclaimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.subclaimsubmission.responses.PagedSubClaimSubmissionResponse;
import gov.va.starter.example.api.subclaimsubmission.responses.SubClaimSubmissionResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.Problem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequestMapping(value = "/v1/example/subclaimsubmissions", produces = "application/json")
@Tag(
    name = "SubClaimSubmission API",
    description = "Starter Kit template API, essentially CRUD access")
@SecurityRequirement(name = "bearer-jwt")
@Timed
public interface SubClaimSubmissionResource {

  @Operation(
      summary = "Create SubClaimSubmission",
      description = "Create a new SubClaimSubmission from the demographic information provided")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created a new SubClaimSubmission",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubClaimSubmissionResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data provided",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Timed(value = "example.subclaimsubmissions.create")
  ResponseEntity<SubClaimSubmissionResponse> addEntity(
      @Parameter(
              description = "metadata for new SubClaimSubmission resource. Cannot null or empty.",
              required = true,
              schema = @Schema(implementation = SubClaimSubmissionRequest.class))
          @Valid
          @RequestBody
          SubClaimSubmissionRequest request)
      throws RequestValidationException;

  @Operation(
      summary = "Find SubClaimSubmission",
      description = "Find a specific SubClaimSubmission by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the SubClaimSubmission",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubClaimSubmissionResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id supplied",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.subclaimsubmissions.findById")
  ResponseEntity<SubClaimSubmissionResponse> findEntityById(
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id)
      throws ResourceNotFoundException;

  @Operation(
      summary = "Get SubClaimSubmissions",
      description = "Get all existing SubClaimSubmissions")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found all existing SubClaimSubmissions",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PagedSubClaimSubmissionResponse.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.subclaimsubmissions.findAll")
  ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> findEntities(
      @Parameter(
              description = "Paging specification for retrieving a subset of the full list.",
              example = "{\"page\": 0, \"size\": 10, \"sort\":[\"id\"]}",
              required = false)
          Pageable pageable);

  @Operation(
      summary = "Update SubClaimSubmission",
      description = "Update info for an existing SubClaimSubmission")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Updated SubClaimSubmission info",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubClaimSubmissionResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid entity",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "404",
            description = "SubClaimSubmission not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.subclaimsubmissions.update")
  ResponseEntity<SubClaimSubmissionResponse> updateEntityById(
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot be null or empty",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
      @Parameter(
              description = "updated metadata SubClaimSubmission resource. Cannot null or empty.",
              required = true,
              schema = @Schema(implementation = SubClaimSubmissionRequest.class))
          @Valid
          @RequestBody
          SubClaimSubmissionRequest request)
      throws ResourceNotFoundException, RequestValidationException;

  @Operation(
      summary = "Delete SubClaimSubmission",
      description = "Delete an existing SubClaimSubmission by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deleted SubClaimSubmission info",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubClaimSubmissionResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id supplied",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Not authorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class))),
        @ApiResponse(
            responseCode = "404",
            description = "SubClaimSubmission not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.subclaimsubmissions.delete")
  ResponseEntity<SubClaimSubmissionResponse> deleteEntityById(
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id)
      throws ResourceNotFoundException;
}
