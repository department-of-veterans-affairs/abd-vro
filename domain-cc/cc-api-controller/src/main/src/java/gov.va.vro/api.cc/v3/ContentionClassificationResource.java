package gov.va.vro.api.cc.v3;

import gov.va.vro.api.cc.ResourceException;
//import gov.va.vro.model.xample.SomeDtoModel;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import gov.va.vro.api.cc.v3.CCRequest;
import gov.va.vro.api.cc.v3.CCResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping(value = "/v3", produces = "application/json")
@Timed("cc")
@Tag(name = "Contention Classification")
public interface ContentionClassificationResource {
  @PostMapping(value = "/domain-cc/{ccEndpoint}")
  @Timed(value = "resource.post")
  @Operation( summary = "Hit python endpoint", description = "n/a")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successful Request"),
        @ApiResponse(
            responseCode = "400",
            description = "CC: Bad Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "401",
            description = "CC: Unauthorized",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "CC: Server Error",
            content = @Content(schema = @Schema(hidden = true)))
      })
  ResponseEntity<CCResponse> postResource( @PathVariable(value = "endpoint") String ccEndpoint )
      throws MethodArgumentNotValidException, ResourceException;

    @GetMapping(value = "/domain-cc/fixed-endpoint")
    @Timed(value = "resource.get")
    @Operation(summary="retrieve from fixed url path", description="description goes here")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Successful Request"),
                @ApiResponse(
                        responseCode = "500",
                        description = "Xample Resource: Server Error",
                        content = @Content(schema = @Schema(hidden = true))),
        })
    ResponseEntity<CCResponse> getFixedPath()
            throws MethodArgumentNotValidException, ResourceException;
}
