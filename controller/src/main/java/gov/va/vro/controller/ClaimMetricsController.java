package gov.va.vro.controller;

import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.resources.ClaimMetricsResource;
import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
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

  @Override
  public ResponseEntity<ClaimMetricsResponse> claimMetrics() {
    ClaimMetricsResponse response = claimMetricsService.getClaimMetrics();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ClaimInfoResponse> claimInfoForClaimId(String claimSubmissionId)
      throws ClaimProcessingException {
    ClaimInfoResponse response = claimMetricsService.findClaimInfo(claimSubmissionId);
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
}
