package gov.va.vro.controller.cc.v1;

// import static gov.va.vro.model.redo.CamelConstants.POST_RESOURCE_QUEUE;
// import static gov.va.vro.model.redo.CamelConstants.V3_EXCHANGE;

import gov.va.vro.api.cc.ResourceException;
import gov.va.vro.api.cc.v1.CCResource;
import gov.va.vro.api.cc.v1.ResourceRequest;
import gov.va.vro.api.cc.v1.ResourceResponse;
import gov.va.vro.camel.CamelEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ContentionClassificationController implements CCResource {
  private final CamelEntry camelEntry;
  @Override
  public ResponseEntity<ResourceResponse> callEndpoint(String endpoint, ResourceRequest request)
      throws ResourceException {
    log.info("callEndpoint logging info");
    try {
      log.info("endpoint received: {}", endpoint);
      log.info(endpoint);
      log.info(endpoint);
      log.info(endpoint);
      log.info("^^^");
      var example_payload = "{ \"endpoint\": \"get_classification\", \"method\": \"POST\", \"payload\": { \"foo\": \"bar\", \"baz\": \"qux\" } }";
      var result = camelEntry.inOut("contention-classification-exchange", "domain-cc-classify", example_payload, String.class);
      log.info("camel result received: {}", result);
      log.info(result);
      log.info(result);
      log.info(result);
      log.info("^^^");
      ResourceResponse response =
          new ResourceResponse("resource_id", "diagnostic", "status", 409, "status_msg");
      return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    } catch (Exception ex) {
      log.error("error in POST request", ex);
      throw new ResourceException(request.getResourceId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }
}
