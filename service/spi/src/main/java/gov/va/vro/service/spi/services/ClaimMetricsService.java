package gov.va.vro.service.spi.services;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;

public interface ClaimMetricsService {
  ClaimMetricsResponse getClaimMetrics();

  ClaimInfoResponse findClaimInfo(String claimSubmissionId, String idType);

  ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params);

  ExamOrdersInfo findAllExamOrderInfo(ExamOrderInfoQueryParams params);

}
