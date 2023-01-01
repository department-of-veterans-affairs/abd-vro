package gov.va.vro.controller;

import gov.va.vro.api.model.MetricsProcessingException;
import gov.va.vro.api.resources.ClaimMetricsResource;
import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public ResponseEntity<List<ClaimInfoResponse>> claimInfoForAll(
      Integer page, Integer size, String icn) throws MetricsProcessingException {
    ClaimInfoQueryParams params =
        ClaimInfoQueryParams.builder().page(page).size(size).icn(icn).build();
    ClaimsInfo claimsInfo = claimMetricsService.findAllClaimInfo(params);
    return ResponseEntity.ok(claimsInfo.getClaimInfoList());
  }
}
