package gov.va.vro.service.spi.services;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.ClaimMetricsInfo;

public interface ClaimMetricsService {
  ClaimMetricsInfo getClaimMetrics();

  ClaimInfoResponse findClaimInfo(String claimSubmissionId);

  ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params);
}
