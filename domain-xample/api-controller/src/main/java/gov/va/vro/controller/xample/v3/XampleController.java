package gov.va.vro.controller.xample.v3;

import static gov.va.vro.model.xample.CamelConstants.GET_RESOURCE_QUEUE;
import static gov.va.vro.model.xample.CamelConstants.POST_RESOURCE_QUEUE;
import static gov.va.vro.model.xample.CamelConstants.V3_EXCHANGE;

import gov.va.vro.api.xample.ResourceException;
import gov.va.vro.api.xample.v3.ResourceRequest;
import gov.va.vro.api.xample.v3.ResourceResponse;
import gov.va.vro.api.xample.v3.XampleResource;
import gov.va.vro.camel.CamelEntry;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class XampleController implements XampleResource {

  private final CamelEntry camelEntry;
  private final ResourceMapper resourceMapper;

  @Override
  public ResponseEntity<ResourceResponse> postResource(ResourceRequest request)
      throws ResourceException {
    log.info(
        "Post XResource for resource: {} and diagnostic code {}",
        request.getResourceId(),
        request.getDiagnosticCode());
    try {
      SomeDtoModel model = resourceMapper.toModel(request);
      log.info("REQUEST to postXResource: {}", model);
      var result = camelEntry.inOut(V3_EXCHANGE, POST_RESOURCE_QUEUE, model, SomeDtoModel.class);
      log.info("RESPONSE from postXResource returned status: {}", result.getStatus());
      ResourceResponse response = resourceMapper.toResourceResponse(result);
      if (StatusValue.ERROR.name().equals(result.getStatus())) {
        log.warn("RESPONSE from postXResource returned error reason: {}", result.getReason());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      log.error("Error in Post XResource", ex);
      throw new ResourceException(request.getResourceId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<SomeDtoModel> getResource(String resourceId) throws ResourceException {
    log.info("Fetching pdf for resource: {}", resourceId);
    try {
      var response =
          camelEntry.inOut(V3_EXCHANGE, GET_RESOURCE_QUEUE, resourceId, SomeDtoModel.class);
      return fetchProcess(resourceId, response);
    } catch (Exception ex) {
      log.error("Error in fetch pdf", ex);
      throw new ResourceException(resourceId, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  private ResponseEntity<SomeDtoModel> fetchProcess(String resourceId, SomeDtoModel response)
      throws IOException {
    log.info("RESPONSE from fetchProcess returned status: {}", response.getStatus());
    if (response.getStatus().equals("NOT_FOUND")) {
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    } else if (response.getStatus().equals("ERROR")) {
      log.info("RESPONSE from fetchProcess returned error reason: {}", response.getReason());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
