package gov.va.vro.service.spi.services;

import gov.va.vro.service.spi.model.ClaimInfoData;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;

import java.util.List;

public interface ClaimMetricsService {

  ClaimMetricsInfo claimMetrics();

  ClaimInfoData claimInfoForClaimId(String claimSubmissionId);

  List<ClaimInfoData> claimInfoForVeteran(String veteranIcn);

  List<ClaimInfoData> claimInfoWithPagination(int offset, int pageSize);
}
