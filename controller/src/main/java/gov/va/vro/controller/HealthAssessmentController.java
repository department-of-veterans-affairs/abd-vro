package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.HealthAssessmentResource;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.services.DiagnosisLookup;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthAssessmentController implements HealthAssessmentResource {

  private final CamelEntrance camelEntrance;
  private final PostClaimRequestMapper postClaimRequestMapper;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ResponseEntity<FullHealthDataAssessmentResponse> postHealthAssessment(
      HealthDataAssessmentRequest claim) throws ClaimProcessingException {
    log.info(
        "Getting full health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    String diagnosis = DiagnosisLookup.getDiagnosis(claim.getDiagnosticCode());
    if (diagnosis == null) {
      throw new ClaimProcessingException(
          claim.getClaimSubmissionId(),
          HttpStatus.BAD_REQUEST,
          String.format(
              "Claim with [diagnosticCode = %s] is not in scope.", claim.getDiagnosticCode()));
    }
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      // PostClaimMapper is used in both v1 (VroController) and v2. To differentiate the path we are
      // on, set the type here. which is v2 for this file.
      model.setIdType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
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
}
