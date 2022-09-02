package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.VroResource;
import gov.va.vro.api.responses.*;
import gov.va.vro.controller.mapper.GeneratePdfRequestMapper;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import gov.va.vro.service.spi.services.FetchClaimsService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VroController implements VroResource {

  private final CamelEntrance camelEntrance;
  private final GeneratePdfRequestMapper generatePdfRequestMapper;
  private final PostClaimRequestMapper postClaimRequestMapper;

  private final FetchClaimsService fetchClaimsService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ResponseEntity<HealthDataAssessmentResponse> postHealthAssessment(
      HealthDataAssessmentRequest claim)
      throws RequestValidationException, ClaimProcessingException {
    log.info(
        "Getting health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaim(model);

      HealthDataAssessmentResponse response =
          objectMapper.readValue(responseAsString, HealthDataAssessmentResponse.class);
      if (response.getEvidence() == null) {
        throw new ClaimProcessingException(
            claim.getClaimSubmissionId(), HttpStatus.NOT_FOUND, "No evidence found.");
      }
      log.info("Returning health assessment for: {}", response.getVeteranIcn());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (ClaimProcessingException cpe) {
      throw cpe;
    } catch (Exception ex) {
      log.error("Error in health assessment", ex);
      throw new ClaimProcessingException(
          claim.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generatePdf(GeneratePdfRequest request)
      throws RequestValidationException, ClaimProcessingException {
    log.info("Generating pdf for claim: {}", request.getClaimSubmissionId());
    try {
      GeneratePdfPayload model = generatePdfRequestMapper.toModel(request);
      log.info("MODEL from generatePdf: {}", model);
      String response = camelEntrance.generatePdf(model);
      log.info("RESPONSE from generatePdf: {}", response);
      model.setPdfDocumentJson(response);
      GeneratePdfResponse responseObj = generatePdfRequestMapper.toGeneratePdfResponse(model);
      return new ResponseEntity<>(responseObj, HttpStatus.OK);
    } catch (Exception ex) {
      log.error("Error in generate pdf", ex);
      throw new ClaimProcessingException(
          request.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<Object> fetchPdf(String claimSubmissionId)
      throws RequestValidationException, ClaimProcessingException {

    log.info("Fetching pdf for claim: {}", claimSubmissionId);
    try {
      String response = camelEntrance.fetchPdf(claimSubmissionId);
      FetchPdfResponse pdfResponse = objectMapper.readValue(response, FetchPdfResponse.class);

      log.info("RESPONSE from fetchPdf: {}", pdfResponse.toString());
      if (pdfResponse.hasContent()) {
        byte[] decoder = Base64.getDecoder().decode(pdfResponse.getPdfData());
        try (InputStream is = new ByteArrayInputStream(decoder)) {
          InputStreamResource resource = new InputStreamResource(is);
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_PDF);

          ContentDisposition disposition =
              ContentDisposition.attachment().filename("textdown.pdf").build();
          headers.setContentDisposition(disposition);
          return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }

      } else {
        return new ResponseEntity<>(pdfResponse, HttpStatus.OK);
      }
    } catch (Exception ex) {
      log.error("Error in fetch pdf", ex);
      throw new ClaimProcessingException(claimSubmissionId, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<FullHealthDataAssessmentResponse> postFullHealthAssessment(
      HealthDataAssessmentRequest claim)
      throws RequestValidationException, ClaimProcessingException {
    log.info(
        "Getting full health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaimFull(model);

      FullHealthDataAssessmentResponse response =
          objectMapper.readValue(responseAsString, FullHealthDataAssessmentResponse.class);
      if (response.getEvidence() == null) {
        throw new ClaimProcessingException(
                claim.getClaimSubmissionId(), HttpStatus.NOT_FOUND, "No evidence found.");
      }
      log.info("Returning health assessment for: {}", claim.getVeteranIcn());
      response.setVeteranIcn(claim.getVeteranIcn());
      response.setDiagnosticCode(claim.getDiagnosticCode());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (ClaimProcessingException cpe) {
      throw cpe;
    } catch (Exception ex) {
      log.error("Error in full health assessment", ex);
      throw new ClaimProcessingException(
          claim.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<FetchClaimsResponse> fetchClaims() {

    try {

      List<Claim> claimList = fetchClaimsService.fetchClaims();
      List<ClaimInfo> claims = new ArrayList<>();
      for (Claim claim : claimList) {
        ClaimInfo info = new ClaimInfo();
        info.setClaimSubmissionId(claim.getClaimSubmissionId());
        info.setVeteranIcn(claim.getVeteranIcn());
        List<String> contentionsList = new ArrayList<>();
        for (String contention : claim.getContentions()) {
          contentionsList.add(contention);
          info.setContentions(contentionsList);
        }
        claims.add(info);
      }
      FetchClaimsResponse response = new FetchClaimsResponse();
      response.setClaims(claims);
      response.setErrorMessage("Success");
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      FetchClaimsResponse failure = new FetchClaimsResponse();
      failure.setErrorMessage("Could not fetch claims from the DB.  " + e.getCause());
      log.error("Could not fetch claims from the DB.  " + e.getCause());
      return new ResponseEntity<>(failure, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
