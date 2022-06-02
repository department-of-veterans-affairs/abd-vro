package gov.va.vro.controller.demo;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.starter.example.api.demo.requests.AssessHealthDataRequest;
import gov.va.starter.example.api.demo.resources.DemoResource;
import gov.va.vro.service.provider.CamelEntrance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController implements DemoResource {

  // https://www.baeldung.com/constructor-injection-in-spring#implicit-constructor-injection
  private final CamelEntrance camelEntrance;

  public DemoController(CamelEntrance camelEntrance) {
    this.camelEntrance = camelEntrance;
  }

  @Override
  public ResponseEntity<String> assess_health_data(AssessHealthDataRequest request)
      throws RequestValidationException {
    String response = camelEntrance.assess_health_data_demo(request);
    log.info("RESPONSE: {}", response);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
