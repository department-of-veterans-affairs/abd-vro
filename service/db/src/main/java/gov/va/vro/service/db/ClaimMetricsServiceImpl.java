package gov.va.vro.service.db;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.db.mapper.ClaimInfoResponseMapper;
import gov.va.vro.service.spi.model.ClaimInfoData;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  private final AssessmentResultRepository assessmentResultRepository;

  private final EvidenceSummaryDocumentRepository evidenceSummaryDocumentRepository;

  private final ClaimInfoResponseMapper claimInfoResponseMapper;

  @Override
  public ClaimMetricsInfo claimMetrics() {
    ClaimMetricsInfo metrics = new ClaimMetricsInfo();
    try {
      metrics.setTotalClaims(claimRepository.count());
      metrics.setAssessmentResults(assessmentResultRepository.count());
      metrics.setEvidenceSummaryDocuments(evidenceSummaryDocumentRepository.count());
      return metrics;
    } catch (Exception e) {
      log.error("Could not get metrics in claim repository.", e);
      metrics.setErrorMessage("Failure;" + e.getMessage());
      return metrics;
    }
  }

  private void setEvidenceSummaryCounts(ClaimEntity claim, ClaimInfoData info) {
    int count = 0;
    for (ContentionEntity contention : claim.getContentions()) {
      count += contention.getEvidenceSummaryDocuments().size();
    }
    info.setEvidenceSummaryDocumentsCount(count);
  }

  @Override
  public ClaimInfoResponse findClaimInfo(String claimSubmissionId) {
    ClaimEntity claim = claimRepository.findByClaimSubmissionId(claimSubmissionId).orElse(null);
    if (claim == null) {
      log.warn("Could not find claim with the claimSubmissionId: {}", claimSubmissionId);
      return null;
    }
    return claimInfoResponseMapper.toClaimInfoResponse(claim);
  }

  private Page<ClaimEntity> findAllClaimInfoPage(ClaimInfoQueryParams params) {
    int size = params.getSize();
    int page = params.getPage();
    String icn = params.getIcn();
    PageRequest pageRequest = PageRequest.of(page, size);
    if (icn == null) {
      return claimRepository.findAll(pageRequest);
    } else {
      return claimRepository.findAllByVeteranIcn(icn, pageRequest);
    }
  }

  @Override
  public ClaimsInfo findAllClaimInfo(ClaimInfoQueryParams params) {
    Page<ClaimEntity> claims = findAllClaimInfoPage(params);
    List<ClaimInfoResponse> claimsInfo = claimInfoResponseMapper.toClaimInfoResponses(claims);
    return new ClaimsInfo(claimsInfo, claims.getTotalElements());
  }
}
