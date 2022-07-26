package gov.va.vro.controller.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.demo.requests.AssessHealthDataRequest;
import gov.va.vro.api.demo.requests.GeneratePdfRequest;
import gov.va.vro.api.demo.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.demo.resources.DemoResource;
import gov.va.vro.api.demo.responses.AssessHealthDataResponse;
import gov.va.vro.api.demo.responses.FetchPdfResponse;
import gov.va.vro.api.demo.responses.GeneratePdfResponse;
import gov.va.vro.api.demo.responses.HealthDataAssessmentResponse;
import gov.va.vro.controller.demo.mapper.AssessHealthDataRequestMapper;
import gov.va.vro.controller.demo.mapper.GenerateDataRequestMapper;
import gov.va.vro.controller.demo.mapper.PostClaimRequestMapper;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import gov.va.vro.service.spi.demo.model.ClaimPayload;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@RestController
public class DemoController implements DemoResource {

  // https://www.baeldung.com/constructor-injection-in-spring#implicit-constructor-injection
  private final CamelEntrance camelEntrance;
  private final AssessHealthDataRequestMapper assess_health_mapper;
  private final GenerateDataRequestMapper generate_pdf_mapper;
  private final PostClaimRequestMapper postClaimRequestMapper;

  public DemoController(
      CamelEntrance camelEntrance,
      AssessHealthDataRequestMapper assess_health_mapper,
      GenerateDataRequestMapper generate_pdf_mapper,
      PostClaimRequestMapper postClaimRequestMapper) {
    this.camelEntrance = camelEntrance;
    this.assess_health_mapper = assess_health_mapper;
    this.generate_pdf_mapper = generate_pdf_mapper;
    this.postClaimRequestMapper = postClaimRequestMapper;
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
  public ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(
      HealthDataAssessmentRequest claim) throws RequestValidationException {
    log.info("Getting health assessment for: {}", claim.getVeteranIcn());
    try {
      ClaimPayload model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaim(model);
      ObjectMapper mapper = new ObjectMapper();
      HealthDataAssessmentResponse response =
          mapper.readValue(responseAsString, HealthDataAssessmentResponse.class);
      log.info("Returning health assessment for: {}", response.getVeteranIcn());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      String msg = ex.getMessage();
      log.error(ex.getStackTrace().toString());
      HealthDataAssessmentResponse response =
          new HealthDataAssessmentResponse(claim.getVeteranIcn(), claim.getDiagnosticCode(), msg);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generate_pdf(GeneratePdfRequest request)
      throws RequestValidationException {
    GeneratePdfPayload model = generate_pdf_mapper.toModel(request);
    String response = camelEntrance.generate_pdf(model);
    log.info("RESPONSE from generate_pdf: {}", response);
    model.setPdfDocumentJson(response);
    GeneratePdfResponse responseObj = generate_pdf_mapper.toGeneratePdfResponse(model);
    return new ResponseEntity<>(responseObj, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Object> fetch_pdf(GeneratePdfRequest request)
      throws RequestValidationException {
    GeneratePdfPayload model = generate_pdf_mapper.toModel(request);
    String response = camelEntrance.fetch_pdf(model);
    FetchPdfResponse pdfResponse = null;
    try {
      pdfResponse = new ObjectMapper().readValue(response, FetchPdfResponse.class);
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    log.info("RESPONSE from fetch_pdf: {}", pdfResponse.toString());
    if (pdfResponse.pdfData.length() > 0) {
      byte[] decoder = Base64.getDecoder().decode(pdfResponse.pdfData);
      InputStream is = new ByteArrayInputStream(decoder);
      InputStreamResource resource = new InputStreamResource(is);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);

      ContentDisposition disposition =
          ContentDisposition.attachment().filename("textdown.pdf").build();
      headers.setContentDisposition(disposition);

      return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
    } else {
      model.setPdfDocumentJson(response);
      GeneratePdfResponse responseObj = generate_pdf_mapper.toGeneratePdfResponse(model);
      return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }
  }
}
