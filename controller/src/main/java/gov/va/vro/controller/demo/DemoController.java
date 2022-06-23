package gov.va.vro.controller.demo;

import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.demo.requests.AssessHealthDataRequest;
import gov.va.vro.api.demo.requests.GeneratePdfRequest;
import gov.va.vro.api.demo.resources.DemoResource;
import gov.va.vro.api.demo.responses.AssessHealthDataResponse;
import gov.va.vro.api.demo.responses.GeneratePdfResponse;
import gov.va.vro.controller.demo.mapper.AssessHealthDataRequestMapper;
import gov.va.vro.controller.demo.mapper.GenerateDataRequestMapper;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController implements DemoResource {

  // https://www.baeldung.com/constructor-injection-in-spring#implicit-constructor-injection
  private final CamelEntrance camelEntrance;
  private final AssessHealthDataRequestMapper assess_health_mapper;
  private final GenerateDataRequestMapper generate_pdf_mapper;

  public DemoController(CamelEntrance camelEntrance,
                        AssessHealthDataRequestMapper assess_health_mapper,
                        GenerateDataRequestMapper generate_pdf_mapper) {
    this.camelEntrance = camelEntrance;
    this.assess_health_mapper = assess_health_mapper;
    this.generate_pdf_mapper = generate_pdf_mapper;
  }

  @Override
  public ResponseEntity<AssessHealthDataResponse> assess_health_data(
      AssessHealthDataRequest request) throws RequestValidationException {
    AssessHealthData model = assess_health_mapper.toModel(request);
    String response = camelEntrance.assess_health_data_demo(model);
    log.info("RESPONSE from assess_health_data_demo: {}", response);
    model.setBpReadingsJson(response);
    AssessHealthDataResponse responseObj = assess_health_mapper.toAssessHealthDataResponse(model);
    return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generate_pdf(
      GeneratePdfRequest request) throws RequestValidationException {
    GeneratePdfPayload model = generate_pdf_mapper.toModel(request);
    String response = camelEntrance.generate_pdf_demo(model);
    log.info("RESPONSE from generate_pdf_demo: {}", response);
    model.setPdfDocumentJson(response);
    GeneratePdfResponse responseObj = generate_pdf_mapper.toGeneratePdfResponse(model);
    return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
  }
}
