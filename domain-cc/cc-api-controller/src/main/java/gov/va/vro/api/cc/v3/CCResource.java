package gov.va.vro.api.cc.v3;

import gov.va.vro.api.cc.ResourceException;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping(value = "/v3", produces = "application/json")
@Timed("cc") // See https://www.baeldung.com/micrometer
@Tag(name = "CC Domain")
public interface CCResource {
  @PostMapping("/domain-cc/{endpoint}")
  @Timed(value = "resource.post")
  ResponseEntity<ResourceResponse> callEndpoint(
      @PathVariable(value = "endpoint") String endpoint,
      @Valid @RequestBody ResourceRequest request)
      throws MethodArgumentNotValidException, ResourceException;
}
