package gov.va.vro.controller.cc.v3;

//import static gov.va.vro.model.cc.CamelConstants.POST_RESOURCE_QUEUE;
//import static gov.va.vro.model.cc.CamelConstants.V3_EXCHANGE;

import gov.va.vro.api.cc.ResourceException;
import gov.va.vro.api.cc.v3.CCRequest;
import gov.va.vro.api.cc.v3.CCResponse;
import gov.va.vro.api.cc.v3.ContentionClassificationResource;
import gov.va.vro.camel.CamelEntry;
//import gov.va.vro.model.cc.SomeDtoModel;
//import gov.va.vro.model.cc.StatusValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ContentionClassificationController implements ContentionClassificationResource {

  private final CamelEntry camelEntry;
  private final ResourceMapper resourceMapper;

  @Override
  public ResponseEntity<CCResponse> postResource(String ccEndpoint)
      throws ResourceException {
    log.info(
        "Post XResource for resource: {} and diagnostic code {}",
//        request.getResourceId(),
//        request.getDiagnosticCode());
    try {
      log.info("ContentionClassificationController postResource!");
      log.info(ccEndpoint);
      log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      log.error("Error in Post XResource", ex);
      throw new ResourceException(request.getResourceId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }
}
