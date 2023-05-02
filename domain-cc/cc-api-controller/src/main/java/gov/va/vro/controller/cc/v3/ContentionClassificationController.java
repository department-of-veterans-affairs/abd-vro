package gov.va.vro.controller.cc.v3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.cc.ResourceException;
import gov.va.vro.api.cc.v3.CCResource;
import gov.va.vro.api.cc.v3.ResourceResponse;
import gov.va.vro.camel.CamelEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ContentionClassificationController implements CCResource {
  private final CamelEntry camelEntry;
  private final String EXCHANGE_NAME = "contention-classification-exchange";
  private final String ENDPOINT_NAME = "domain-cc-classify";

  // Get a POJO from the Camel response so jackson can automagically serialize the HTTP response
  // https://stackoverflow.com/a/44842806
  // https://stackoverflow.com/a/45465724
  public HashMap<String, Object> getMapFromString(String jsonString) {
    final ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> mapFromString = new HashMap<>();
    try {
      mapFromString = mapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
      });
    } catch (IOException e) {
      log.error("Exception launched while trying to parse String to Map.", e);
    }
    return mapFromString;
  }

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
      var resultResponseBodyObject = getMapFromString(result).get("response_body");

      ResourceResponse response =
          new ResourceResponse(statusCode, resultResponseBodyObject);

      return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    } catch (Exception ex) {
      log.error("error in POST request", ex);
      throw new ResourceException(request.toString(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }
}
