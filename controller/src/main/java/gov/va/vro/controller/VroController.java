package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.VroResource;
import gov.va.vro.api.responses.FetchPdfResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.api.responses.HealthData7101AssessmentResponse;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.controller.mapper.FetchPdfRequestMapper;
import gov.va.vro.controller.mapper.GeneratePdfRequestMapper;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import gov.va.vro.service.spi.model.SimpleClaim;
import gov.va.vro.service.spi.services.fetchclaims.FetchClaimsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
public class VroController implements VroResource {

  // https://www.baeldung.com/constructor-injection-in-spring#implicit-constructor-injection
  private final CamelEntrance camelEntrance;
  private final GeneratePdfRequestMapper generatePdfRequestMapper;
  private final FetchPdfRequestMapper fetchPdfRequestMapper;
  private final PostClaimRequestMapper postClaimRequestMapper;

  private final FetchClaimsService fetchClaimsService;

  public VroController(
      CamelEntrance camelEntrance,
      GeneratePdfRequestMapper generatePdfRequestMapper,
      FetchPdfRequestMapper fetchPdfRequestMapper,
      PostClaimRequestMapper postClaimRequestMapper,
      FetchClaimsService fetchClaimsService) {
    this.camelEntrance = camelEntrance;
    this.generatePdfRequestMapper = generatePdfRequestMapper;
    this.fetchPdfRequestMapper = fetchPdfRequestMapper;
    this.postClaimRequestMapper = postClaimRequestMapper;
    this.fetchClaimsService = fetchClaimsService;
  }

  @Override
  public ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(
      HealthDataAssessmentRequest claim) throws RequestValidationException {
    log.info("Getting health assessment for: {}", claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaim(model);
      ObjectMapper mapper = new ObjectMapper();
      HealthDataAssessmentResponse response =
          mapper.readValue(responseAsString, HealthDataAssessmentResponse.class);
      log.info("Returning health assessment for: {}", response.getVeteranIcn());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      String msg = ex.getMessage();
      log.error("Error in health assessment", ex);
      HealthDataAssessmentResponse response =
          new HealthDataAssessmentResponse(claim.getVeteranIcn(), claim.getDiagnosticCode(), msg);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generatePdf(GeneratePdfRequest request)
      throws RequestValidationException {
    GeneratePdfPayload model = generatePdfRequestMapper.toModel(request);
    log.info("MODEL from generatePdf: {}", model);
    String response = camelEntrance.generatePdf(model);
    log.info("RESPONSE from generatePdf: {}", response);
    model.setPdfDocumentJson(response);
    GeneratePdfResponse responseObj = generatePdfRequestMapper.toGeneratePdfResponse(model);
    return new ResponseEntity<>(responseObj, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Object> fetchPdf(String claimSubmissionId)
      throws RequestValidationException {
    String response = camelEntrance.fetchPdf(claimSubmissionId);
    FetchPdfResponse pdfResponse = null;
    try {
      pdfResponse = new ObjectMapper().readValue(response, FetchPdfResponse.class);
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    log.info("RESPONSE from fetchPdf: {}", pdfResponse.toString());
    if (pdfResponse.pdfData.length() > 0) {
      byte[] decoder = Base64.getDecoder().decode(pdfResponse.pdfData);
      InputStream is = new ByteArrayInputStream(decoder);
      InputStreamResource resource = new InputStreamResource(is);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);

      ContentDisposition disposition =
          ContentDisposition.attachment().filename("textdown.pdf").build();
      headers.setContentDisposition(disposition);

      return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(pdfResponse.toString(), HttpStatus.OK);
    }
  }

  @Override
  public ResponseEntity<HealthData7101AssessmentResponse> postHealth7101Assessment(
      HealthDataAssessmentRequest claim) throws RequestValidationException {
    log.info("Getting health assessment for: {}", claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaimFull(model);
      log.info("Obtained full health assessment", responseAsString);
      ObjectMapper mapper = new ObjectMapper();
      HealthData7101AssessmentResponse response =
          mapper.readValue(responseAsString, HealthData7101AssessmentResponse.class);
      log.info("Returning health assessment for: {}", claim.getVeteranIcn());
      response.setVeteranIcn(claim.getVeteranIcn());
      response.setDiagnosticCode(claim.getDiagnosticCode());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception ex) {
      String msg = ex.getMessage();
      log.error("Error in health 7101 assessment", ex);
      HealthData7101AssessmentResponse response =
          new HealthData7101AssessmentResponse(
              claim.getVeteranIcn(), claim.getDiagnosticCode(), msg);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<Object> fetchClaims() {
    List<SimpleClaim> claimList = new ArrayList<>();
    try {
      claimList = fetchClaimsService.fetchClaims();
    } catch (Exception e) {
      log.error("Could not fetch all claims or contentions");
    }
    return new ResponseEntity<>(claimList, HttpStatus.OK);
  }
}
