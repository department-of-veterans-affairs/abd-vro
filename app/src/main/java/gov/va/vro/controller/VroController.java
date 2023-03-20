package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.VroResource;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.controller.mapper.GeneratePdfRequestMapper;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class VroController implements VroResource {

  private final CamelEntrance camelEntrance;
  private final GeneratePdfRequestMapper generatePdfRequestMapper;
  private final PostClaimRequestMapper postClaimRequestMapper;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Fetch PDF and turn it into an HTTP response.
   *
   * @param claimSubmissionId Claim submission ID
   * @param response response
   * @return PDF document
   * @throws ClaimProcessingException PDF document not found
   */
  public ResponseEntity fetchProcess(String claimSubmissionId, String response)
      throws ClaimProcessingException {
    try {
      FetchPdfResponse pdfResponse = objectMapper.readValue(response, FetchPdfResponse.class);
      log.info("RESPONSE from fetchPdf returned status: {}", pdfResponse.getStatus());
      if (pdfResponse.hasContent()) {
        byte[] decoder = Base64.getDecoder().decode(pdfResponse.getPdfData());
        try (InputStream is = new ByteArrayInputStream(decoder)) {
          InputStreamResource resource = new InputStreamResource(is);
          String diagnosis = StringUtils.capitalize(pdfResponse.getDiagnosis());
          HttpHeaders headers = getHttpHeaders(diagnosis);
          return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }
      } else {
        if (pdfResponse.getStatus().equals("NOT_FOUND")) {
          return new ResponseEntity<>(pdfResponse, HttpStatus.BAD_REQUEST);
        } else if (pdfResponse.getStatus().equals("ERROR")) {
          log.info("RESPONSE from generatePdf returned error reason: {}", pdfResponse.getReason());
          return new ResponseEntity<>(pdfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(pdfResponse, HttpStatus.OK);
      }
    } catch (Exception ex) {
      log.error("Error in fetch pdf", ex);
      throw new ClaimProcessingException(claimSubmissionId, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<GeneratePdfResponse> generatePdf(GeneratePdfRequest request)
      throws ClaimProcessingException {
    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        request.getClaimSubmissionId(),
        request.getDiagnosticCode());
    try {
      GeneratePdfPayload model = generatePdfRequestMapper.toModel(request);
      if (model.getPdfTemplate() == null
          || model.getPdfTemplate().isEmpty()
          || model.getPdfTemplate().isBlank()) {
        model.setPdfTemplate("v1");
      }
      log.info(model.toString());
      String response = camelEntrance.generatePdf(model);
      GeneratePdfResponse pdfResponse = objectMapper.readValue(response, GeneratePdfResponse.class);
      log.info(pdfResponse.toString());
      log.info("RESPONSE from generatePdf returned status: {}", pdfResponse.getStatus());
      if (pdfResponse.getStatus().equals("ERROR")) {
        log.info("RESPONSE from generatePdf returned error reason: {}", pdfResponse.getReason());
        return new ResponseEntity<>(pdfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(pdfResponse, HttpStatus.OK);
    } catch (Exception ex) {
      log.error("Error in generate pdf", ex);
      throw new ClaimProcessingException(
          request.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<Object> fetchPdf(String claimSubmissionId) throws ClaimProcessingException {

    log.info("Fetching pdf for claim: {}", claimSubmissionId);
    try {
      String response = camelEntrance.fetchPdf(claimSubmissionId);
      return fetchProcess(claimSubmissionId, response);
    } catch (Exception ex) {
      log.error("Error in fetch pdf", ex);
      throw new ClaimProcessingException(claimSubmissionId, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<Object> immediatePdf(GeneratePdfRequest request)
      throws ClaimProcessingException {
    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        request.getClaimSubmissionId(),
        request.getDiagnosticCode());
    try {
      GeneratePdfPayload model = generatePdfRequestMapper.toModel(request);
      log.info(model.toString());
      String response = camelEntrance.immediatePdf(model);
      return fetchProcess(request.getClaimSubmissionId(), response);
    } catch (Exception ex) {
      log.error("Error in generate fetch pdf", ex);
      throw new ClaimProcessingException(
          request.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  @Override
  public ResponseEntity<FullHealthDataAssessmentResponse> postFullHealthAssessment(
      HealthDataAssessmentRequest claim) throws ClaimProcessingException {
    log.info(
        "Getting full health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaimFull(model);

      AbdEvidenceWithSummary response =
          objectMapper.readValue(responseAsString, AbdEvidenceWithSummary.class);
      if (response.getEvidence() == null) {
        log.info(
            "Response from condition processor returned error message: {}",
            response.getErrorMessage());
        throw new ClaimProcessingException(
            claim.getClaimSubmissionId(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal error while processing claim data.");
      }
      FullHealthDataAssessmentResponse httpResponse =
          objectMapper.convertValue(response, FullHealthDataAssessmentResponse.class);
      log.info("Returning health assessment for: {}", claim.getVeteranIcn());
      httpResponse.setVeteranIcn(claim.getVeteranIcn());
      httpResponse.setDiagnosticCode(claim.getDiagnosticCode());
      return new ResponseEntity<>(httpResponse, HttpStatus.CREATED);
    } catch (Exception ex) {
      log.error("Error in full health assessment", ex);
      throw new ClaimProcessingException(
          claim.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

  private static HttpHeaders getHttpHeaders(String diagnosis) {

    ContentDisposition disposition =
        ContentDisposition.attachment()
            .filename(GeneratePdfPayload.createPdfFilename(diagnosis))
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(disposition);
    return headers;
  }
}
