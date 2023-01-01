package gov.va.vro.service.spi.services;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.service.spi.model.ClaimInfoData;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;

import java.util.List;

public interface ClaimMetricsService {

  ClaimMetricsInfo claimMetrics();

  ClaimInfoResponse findClaimInfo(String claimSubmissionId);

  ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params);
}
