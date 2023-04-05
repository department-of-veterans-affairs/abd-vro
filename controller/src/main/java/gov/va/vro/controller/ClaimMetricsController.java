package gov.va.vro.controller;

import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.resources.ClaimMetricsResource;
import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.services.ClaimMetricsCamelService;
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

  private final ClaimMetricsCamelService claimMetricsCamelService;

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
  public ResponseEntity<List<ExamOrderInfoResponse>> examOrderSlack(Integer page, Integer size) {
    ExamOrderInfoQueryParams params =
        ExamOrderInfoQueryParams.builder().page(page).size(size).build();
    ExamOrdersInfo examOrdersInfo = claimMetricsService.findAllExamOrderInfo(params);
    claimMetricsCamelService.examOrderSlack(examOrdersInfo);
    return ResponseEntity.ok(examOrdersInfo.getExamOrderInfoList());
  }
}
