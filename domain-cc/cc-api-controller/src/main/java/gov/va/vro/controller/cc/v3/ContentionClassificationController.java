package gov.va.vro.controller.cc.v3;

// import static gov.va.vro.model.redo.CamelConstants.POST_RESOURCE_QUEUE;
// import static gov.va.vro.model.redo.CamelConstants.V3_EXCHANGE;

import com.fasterxml.jackson.databind.JsonNode;
import gov.va.vro.api.cc.ResourceException;
import gov.va.vro.api.cc.v3.CCResource;
import gov.va.vro.api.cc.v3.ResourceResponse;
import gov.va.vro.camel.CamelEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ContentionClassificationController implements CCResource {
  private final CamelEntry camelEntry;
  private final String EXCHANGE_NAME = "contention-classification-exchange";
  private final String ENDPOINT_NAME = "domain-cc-classify";

  @Override
  public ResponseEntity<ResourceResponse> callEndpoint(String endpoint, JsonNode request)
          throws ResourceException {
    log.info("callEndpoint logging info");
    try {
      log.info("endpoint received: {}", endpoint);
      var payload_for_cc = new JSONObject();
      payload_for_cc.put("endpoint", endpoint);
      payload_for_cc.put("method", "POST");
      payload_for_cc.put("payload", request);
      log.info("sending this to the RabbitMQ: {}", payload_for_cc);

      var result = camelEntry.inOut(EXCHANGE_NAME, ENDPOINT_NAME, payload_for_cc.toString(), String.class);
      log.info("camel result received: {}", result);
      var result_json = new JSONObject(result);
      var statusCode = result_json.getInt("status_code");

      ResourceResponse response =
          new ResourceResponse(statusCode, result_json.getJSONObject("response_body"));

      return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    } catch (Exception ex) {
      log.error("error in POST request", ex);
      throw new ResourceException(request.toString(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }
}
