package gov.va.vro.api.redo.v3;

import gov.va.vro.api.redo.ResourceException;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;

@RequestMapping(value = "/v3", produces = "application/json")
@Timed("redo") // See https://www.baeldung.com/micrometer
@Tag(name = "Redo Domain")
public interface RedoResource {
  @PostMapping("/redo-resource")
  @Timed(value = "resource.post")
  @Operation(
      summary = "Create Redo Resource",
      description =
          "Generates a Redo Resource for a specific payload."
              + "The created resource will be available using the GET endpoint.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Redo Resource: Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Redo Resource: Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Redo Resource: Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  ResponseEntity<ResourceResponse> postResource(
      @Parameter(
              description = "payload for resource",
              required = true,
              schema = @Schema(implementation = ResourceRequest.class))
          @Valid
          @RequestBody
          ResourceRequest request)
      throws MethodArgumentNotValidException, ResourceException;

  @PostMapping("/domain-redo/redo-post-nothing")
  @Timed(value = "resource.post")
  @Operation(summary = "Create Redo Resource", description = "Generates nothing")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Redo Resource: Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Redo Resource: Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Redo Resource: Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  ResponseEntity<ResourceResponse> postNothing(
      @Parameter(
              description = "payload for resource",
              required = true,
              schema = @Schema(implementation = ResourceRequest.class))
          @Valid
          @RequestBody
          ResourceRequest request)
      throws MethodArgumentNotValidException, ResourceException;

  @PostMapping("/domain-redo/cmd/{endpoint}")
  @Timed(value = "resource.post")
  @Operation(summary = "Call Redo Endpoint", description = "nothing really")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Successful Request"),
                  @ApiResponse(
                          responseCode = "400",
                          description = "Redo Resource: Bad Request",
                          content = @Content(schema = @Schema(hidden = true))),
                  @ApiResponse(
                          responseCode = "401",
                          description = "Redo Resource: Unauthorized",
                          content = @Content(schema = @Schema(hidden = true))),
                  @ApiResponse(
                          responseCode = "500",
                          description = "Redo Resource: Server Error",
                          content = @Content(schema = @Schema(hidden = true)))
          })
  ResponseEntity<ResourceResponse> callEndpoint(
          @PathVariable(value = "endpoint") String endpoint,
          @Parameter(
                  description = "payload for cmd",
                  required = true,
                  schema = @Schema(implementation = ResourceRequest.class))
          @Valid
          @RequestBody
          ResourceRequest request)
          throws MethodArgumentNotValidException, ResourceException;
}
