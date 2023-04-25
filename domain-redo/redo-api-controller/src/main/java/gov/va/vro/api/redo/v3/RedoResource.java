package gov.va.vro.api.redo.v3;

import gov.va.vro.api.redo.ResourceException;
import io.micrometer.core.annotation.Timed;
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
  @PostMapping("/domain-redo/cmd/{endpoint}")
  @Timed(value = "resource.post")
  ResponseEntity<ResourceResponse> callEndpoint(
          @PathVariable(value = "endpoint") String endpoint,
          @Valid
          @RequestBody
          ResourceRequest request)
          throws MethodArgumentNotValidException, ResourceException;
}
