package gov.va.vro.service.spi.services;

import gov.va.vro.model.rrd.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ClaimsInfo;
import gov.va.vro.model.rrd.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimMetricsResponse;

public interface ClaimMetricsService {
  ClaimMetricsResponse getClaimMetrics();

  ClaimInfoResponse findClaimInfo(String claimSubmissionId, String idType);

  ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params);

  ExamOrdersInfo findExamOrderInfo(ExamOrderInfoQueryParams params);
}
