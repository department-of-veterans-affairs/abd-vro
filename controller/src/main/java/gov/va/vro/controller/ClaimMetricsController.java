package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.ClaimMetricsResource;
import gov.va.vro.api.responses.FullHealthDataAssessmentResponse;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.model.AbdEvidenceWithSummary;
import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.services.DiagnosisLookup;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class ClaimMetricsController implements ClaimMetricsResource {
  private final ClaimMetricsService claimMetricsService;
  private final PostClaimRequestMapper postClaimRequestMapper;
  private final ObjectMapper objectMapper;
  private final CamelEntrance camelEntrance;

  @Override
  public ResponseEntity<ClaimMetricsResponse> claimMetrics() {
    ClaimMetricsResponse response = claimMetricsService.getClaimMetrics();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimInfoResponse> claimInfoForClaimId(
      String claimSubmissionId, String claimVersion) throws ClaimProcessingException {
    String idType = MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE;
    if (claimVersion != null) {
      switch (claimVersion) {
        case "v1" -> idType = Claim.V1_ID_TYPE;
        case "v2" -> idType = MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE;
        default -> {
          log.warn("Invalid version given to claim info. Must be v1 or v2 if given");
          String msg = HttpStatus.BAD_REQUEST.getReasonPhrase();
          throw new ClaimProcessingException(claimSubmissionId, HttpStatus.BAD_REQUEST, msg);
        }
      }
    }
    ClaimInfoResponse response = claimMetricsService.findClaimInfo(claimSubmissionId, idType);
    if (response == null) {
      log.warn("Claim {} not found", claimSubmissionId);
      String msg = HttpStatus.NOT_FOUND.getReasonPhrase();
      throw new ClaimProcessingException(claimSubmissionId, HttpStatus.NOT_FOUND, msg);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<ClaimInfoResponse>> claimInfoForAll(
      Integer page, Integer size, String icn) {
    ClaimInfoQueryParams params =
        ClaimInfoQueryParams.builder().page(page).size(size).icn(icn).build();
    ClaimsInfo claimsInfo = claimMetricsService.findAllClaimInfo(params);
    return ResponseEntity.ok(claimsInfo.getClaimInfoList());
  }

  @Override
  public ResponseEntity<List<ExamOrderInfoResponse>> allExamOrderInfo(Integer page, Integer size) {
    ExamOrderInfoQueryParams params =
        ExamOrderInfoQueryParams.builder().page(page).size(size).build();
    ExamOrdersInfo examOrdersInfo = claimMetricsService.findAllExamOrderInfo(params);
    return ResponseEntity.ok(examOrdersInfo.getExamOrderInfoList());
  }

  @Override
  public ResponseEntity<FullHealthDataAssessmentResponse> healthEvidence(
      HealthDataAssessmentRequest claim) throws ClaimProcessingException {
    log.info(
        "Getting full health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    String diagnosis = DiagnosisLookup.getDiagnosis(claim.getDiagnosticCode());
    if (diagnosis == null) {
      throw new ClaimProcessingException(
          claim.getDiagnosticCode(),
          HttpStatus.BAD_REQUEST,
          String.format(
              "Claim with [diagnosticCode = %s] is not in scope.", claim.getDiagnosticCode()));
    }
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      model.setIdType(MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
      String responseAsString = camelEntrance.getHealthEvidence(model);
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
