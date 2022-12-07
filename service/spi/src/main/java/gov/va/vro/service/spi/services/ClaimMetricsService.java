package gov.va.vro.service.spi.services;

import gov.va.vro.service.spi.model.ClaimMetricsInfo;

import java.util.List;

public interface ClaimMetricsService {
  List<ClaimMetricsInfo> claimMetrics(String claimSubmissionId);

  List<ClaimMetricsInfo> claimInfoForVeteran(String veteranIcn);
}
