package gov.va.vro.api.cc.v3;

import com.fasterxml.jackson.databind.JsonNode;
import gov.va.vro.api.cc.ResourceException;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(value = "/v3", produces = "application/json")
@Timed("cc") // See https://www.baeldung.com/micrometer
@Tag(name = "CC Domain")
public interface CCResource {
  @PostMapping("/contention-classification/{endpoint}")
  @Operation(
      summary = "Invoke contention classification python endpoint",
      description =
              "See more detailed documentation here: https://github.com/department-of-veterans-affairs/abd-vro/blob/develop/domain-cc/README.md#building-docs")
  @Timed(value = "resource.post")
  @ResponseBody
  ResponseEntity<ResourceResponse> callEndpoint(
      @PathVariable(value = "endpoint") String endpoint, @Valid @RequestBody JsonNode request)
      throws MethodArgumentNotValidException, ResourceException;
}
