package gov.va.starter.example.api.claimsubmission.resources;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.boot.exception.ResourceNotFoundException;
import gov.va.starter.example.api.claimsubmission.requests.ClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.requests.SubClaimSubmissionRequest;
import gov.va.starter.example.api.claimsubmission.responses.ClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.PagedClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.PagedSubClaimSubmissionResponse;
import gov.va.starter.example.api.claimsubmission.responses.SubClaimSubmissionResponse;
import gov.va.starter.example.api.responses.PagedResponse;
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

@RequestMapping(value = "/v1/example/claimsubmissions", produces = "application/json")
@Tag(
    name = "ClaimSubmission API",
    description = "Starter Kit template API, essentially CRUD access")
@SecurityRequirement(name = "bearer-jwt")
@Timed
public interface ClaimSubmissionResource {
  @Operation(
      summary = "Create ClaimSubmission",
      description = "Create a new ClaimSubmission from the demographic information provided")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created a new ClaimSubmission",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimSubmissionResponse.class))
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
  @Timed(value = "example.claimsubmissions.create")
  ResponseEntity<ClaimSubmissionResponse> addEntity(
      @Parameter(
              description = "metadata for new ClaimSubmission resource. Cannot null or empty.",
              required = true,
              schema = @Schema(implementation = ClaimSubmissionRequest.class))
          @Valid
          @RequestBody
          ClaimSubmissionRequest request)
      throws RequestValidationException;

  @Operation(
      summary = "Find ClaimSubmission",
      description = "Find a specific ClaimSubmission by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the ClaimSubmission",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimSubmissionResponse.class))
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
  @Timed(value = "example.claimsubmissions.findById")
  ResponseEntity<ClaimSubmissionResponse> findEntityById(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id)
      throws ResourceNotFoundException;

  @Operation(summary = "Get ClaimSubmissions", description = "Get all existing ClaimSubmissions")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found all existing ClaimSubmissions",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PagedClaimSubmissionResponse.class))
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
  @Timed(value = "example.claimsubmissions.findAll")
  ResponseEntity<PagedResponse<ClaimSubmissionResponse>> findEntities(
      @Parameter(
              description = "Paging specification for retrieving a subset of the full list.",
              example = "{\"page\": 0, \"size\": 10, \"sort\":[\"id\"]}",
              required = false)
          Pageable pageable);

  @Operation(
      summary = "Update ClaimSubmission",
      description = "Update info for an existing ClaimSubmission")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Updated ClaimSubmission info",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimSubmissionResponse.class))
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
            description = "ClaimSubmission not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.update")
  ResponseEntity<ClaimSubmissionResponse> updateEntityById(
      @Parameter(
              description =
                  "unique identifier for ClaimSubmission resource. Cannot be null or empty",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
      @Parameter(
              description = "updated metadata ClaimSubmission resource. Cannot null or empty.",
              required = true,
              schema = @Schema(implementation = ClaimSubmissionRequest.class))
          @Valid
          @RequestBody
          ClaimSubmissionRequest request)
      throws ResourceNotFoundException, RequestValidationException;

  @Operation(
      summary = "Delete ClaimSubmission",
      description = "Delete an existing ClaimSubmission by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deleted ClaimSubmission info",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ClaimSubmissionResponse.class))
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
            description = "ClaimSubmission not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.delete")
  ResponseEntity<ClaimSubmissionResponse> deleteEntityById(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id)
      throws ResourceNotFoundException;

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
  @PostMapping("/{id}/subclaimsubmissions")
  @ResponseStatus(HttpStatus.CREATED)
  @Timed(value = "example.claimsubmissions.subclaimsubmissions.create")
  ResponseEntity<SubClaimSubmissionResponse> addSubClaimSubmission(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
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
  @GetMapping("/{id}/subclaimsubmissions/{subResourceId}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.subclaimsubmissions.findById")
  ResponseEntity<SubClaimSubmissionResponse> getSubClaimSubmission(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "subResourceId")
          String subResourceId)
      throws ResourceNotFoundException;

  @Operation(
      summary = "Get associated SubClaimSubmissions",
      description = "Get all SubClaimSubmissions related to a specific ClaimSubmission")
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
  @GetMapping("/{id}/subclaimsubmissions")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.subclaimsubmissions.findAll")
  ResponseEntity<PagedResponse<SubClaimSubmissionResponse>> getSubClaimSubmissions(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
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
            description = "ClaimSubmission not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @PutMapping("/{id}/subclaimsubmissions/{subResourceId}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.subclaimsubmissions.update")
  ResponseEntity<SubClaimSubmissionResponse> updateSubClaimSubmission(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "subResourceId")
          String subResourceId,
      @Parameter(
              description = "metadata for new SubClaimSubmission resource. Cannot null or empty.",
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
            description = "Not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Problem.class)))
      })
  @DeleteMapping("/{id}/subclaimsubmissions/{subResourceId}")
  @ResponseStatus(HttpStatus.OK)
  @Timed(value = "example.claimsubmissions.subclaimsubmissions.delete")
  ResponseEntity<SubClaimSubmissionResponse> deleteSubClaimSubmission(
      @Parameter(
              description = "unique identifier for ClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "id")
          String id,
      @Parameter(
              description =
                  "unique identifier for SubClaimSubmission resource. Cannot null or empty.",
              example = "uuid",
              required = true)
          @NotNull
          @PathVariable(value = "subResourceId")
          String subResourceId)
      throws ResourceNotFoundException;
}
