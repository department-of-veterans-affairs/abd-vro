package gov.va.vro.api.xample.v3;

import gov.va.vro.api.xample.ResourceException;
import gov.va.vro.model.xample.SomeDtoModel;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping(value = "/v3", produces = "application/json")
@Timed("xample") // See https://www.baeldung.com/micrometer
@Tag(name = "Xample Domain")
public interface XampleResource {
  @PostMapping("/xample-resource")
  @Timed(value = "resource.post")
  @Operation(
      summary = "Create Xample Resource",
      description =
          "Generates a Xample Resource for a specific payload."
              + "The created resource will be available using the GET endpoint.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Xample Resource: Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Xample Resource: Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Xample Resource: Server Error",
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

  @GetMapping(value = "/xample-resource/{resourceId}")
  @Timed(value = "resource.get")
  @Operation(
      summary = "Retrieve a Xample Resource",
      description = "Retrieves a Xample Resource with the specified ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "Xample Resource: Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "Xample Resource: Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "Xample Resource: Server Error",
            content = @Content(schema = @Schema(hidden = true))),
      })
  ResponseEntity<SomeDtoModel> getResource(@PathVariable String resourceId)
      throws MethodArgumentNotValidException, ResourceException;
}
