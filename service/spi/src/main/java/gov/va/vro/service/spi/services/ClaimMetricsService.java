package gov.va.vro.service.spi.services;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;

public interface ClaimMetricsService {
  ClaimMetricsResponse getClaimMetrics();

  ClaimsInfo findClaimInfo(String claimSubmissionId);

  ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params);
}
