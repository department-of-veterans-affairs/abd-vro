package gov.va.vro.service.db;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  @Override
  public List<ClaimMetricsInfo> claimMetrics(String claimSubmissionId) {
    List<ClaimMetricsInfo> infoList = new ArrayList<>();
    ClaimMetricsInfo info = new ClaimMetricsInfo();
    try {
      ClaimEntity claim = claimRepository.findByClaimSubmissionId(claimSubmissionId).orElseThrow();
      info.setClaimSubmissionId(claim.getClaimSubmissionId());
      info.setVeteranIcn(claim.getVeteran().getIcn());
      info.setContentionsCount(claim.getContentions().size());
      setContentionsList(claim, info);
      setAssessmentResultsAndCount(claim, info);
      setEvidenceSummaryCounts(claim, info);
      infoList.add(info);
    } catch (Exception e) {
      log.error("Could not find claim with the given claimSubmissionId");
      throw new NoSuchElementException("Could not find claim with the given claimSubmissionId");
    }
    return infoList;
  }

  @Override
  public List<ClaimMetricsInfo> claimInfoForVeteran(String veteranIcn) {
    List<ClaimMetricsInfo> veteranClaims = new ArrayList<>();
    ClaimMetricsInfo info = new ClaimMetricsInfo();
    try {
      List<ClaimEntity> claims = claimRepository.findAllByVeteranIcn(veteranIcn);
      for (ClaimEntity claim : claims) {
        info.setVeteranIcn(veteranIcn);
        info.setClaimSubmissionId(claim.getClaimSubmissionId());
        info.setContentionsCount(claim.getContentions().size());
        setContentionsList(claim, info);
        setAssessmentResultsAndCount(claim, info);
        setEvidenceSummaryCounts(claim, info);
        veteranClaims.add(info);
      }
      return veteranClaims;
    } catch (Exception e) {
      log.error("Could not find claim with the given claimSubmissionId");
      throw new NoSuchElementException("Could not find claim with the given claimSubmissionId");
    }
  }

  private void setContentionsList(ClaimEntity entity, ClaimMetricsInfo info) {
    List<String> diagnosticCodes = new ArrayList<>();
    for (ContentionEntity contention : entity.getContentions()) {
      diagnosticCodes.add(contention.getDiagnosticCode());
    }
    info.setContentions(diagnosticCodes);
  }

  private void setAssessmentResultsAndCount(ClaimEntity entity, ClaimMetricsInfo info) {
    int count = 0;
    for (ContentionEntity contention : entity.getContentions()) {
      for (AssessmentResultEntity assessmentResult : contention.getAssessmentResults()) {
        info.setEvidenceSummary(assessmentResult.getEvidenceCountSummary());
        count++;
      }
    }
    info.setAssessmentResultsCount(count);
  }

  private void setEvidenceSummaryCounts(ClaimEntity claim, ClaimMetricsInfo info) {
    int count = 0;
    for (ContentionEntity contention : claim.getContentions()) {
      count = contention.getEvidenceSummaryDocuments().size();
    }
    info.setEvidenceSummaryDocumentsCount(count);
  }
}
