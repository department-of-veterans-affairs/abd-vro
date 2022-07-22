package gov.va.vro.controller.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.demo.model.AbdClaim;
import gov.va.vro.api.demo.model.HealthDataAssessmentResponse;
import gov.va.vro.api.demo.requests.GeneratePdfRequest;
import gov.va.vro.api.demo.resources.DemoResource;
import gov.va.vro.api.demo.responses.GeneratePdfResponse;
import gov.va.vro.controller.demo.mapper.GenerateDataRequestMapper;
import gov.va.vro.service.provider.CamelEntrance;
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
  private final GenerateDataRequestMapper generate_pdf_mapper;

  public DemoController(
      CamelEntrance camelEntrance, GenerateDataRequestMapper generate_pdf_mapper) {
    this.camelEntrance = camelEntrance;
    this.generate_pdf_mapper = generate_pdf_mapper;
  }

  @Override
  public ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(AbdClaim claim)
      throws RequestValidationException {
    log.info("Getting health assessment for: {}", claim.getVeteranIcn());
    try {
      ObjectMapper mapper = new ObjectMapper();
      String claimAsString = mapper.writeValueAsString(claim);
      String responseAsString = camelEntrance.assessHealthData(claimAsString);
      HealthDataAssessmentResponse response =
          mapper.readValue(responseAsString, HealthDataAssessmentResponse.class);
      log.info("Returning health assessment for: {}", response.getVeteranIcn());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      String msg = ex.getMessage();
      log.error(ex.getStackTrace().toString());
      HealthDataAssessmentResponse response = new HealthDataAssessmentResponse(claim, msg);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generate_pdf(GeneratePdfRequest request)
      throws RequestValidationException {
    GeneratePdfPayload model = generate_pdf_mapper.toModel(request);
    String response = camelEntrance.generate_pdf_demo(model);
    log.info("RESPONSE from generate_pdf_demo: {}", response);
    model.setPdfDocumentJson(response);
    GeneratePdfResponse responseObj = generate_pdf_mapper.toGeneratePdfResponse(model);
    return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
  }
}
