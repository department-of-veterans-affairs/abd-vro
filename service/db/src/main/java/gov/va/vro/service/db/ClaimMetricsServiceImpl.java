package gov.va.vro.service.db;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.model.ClaimInfoData;

import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
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

  @Override
  public List<ClaimInfoData> claimInfoForClaimId(String claimSubmissionId) {
    List<ClaimInfoData> infoList = new ArrayList<>();
    try {
      ClaimEntity claim = claimRepository.findByClaimSubmissionId(claimSubmissionId).orElseThrow();
      ClaimInfoData info = new ClaimInfoData();
      info.setClaimSubmissionId(claim.getClaimSubmissionId());
      info.setVeteranIcn(claim.getVeteran().getIcn());
      info.setContentionsCount(claim.getContentions().size());
      setContentionsList(claim, info);
      setAssessmentResultsAndCount(claim, info);
      setEvidenceSummaryCounts(claim, info);
      infoList.add(info);
      return infoList;
    } catch (Exception e) {
      log.error("Could not find claim with the given claimSubmissionId");
      ClaimInfoData info = new ClaimInfoData();
      info.setErrorMessage(
          "Could not find claim with the given claimSubmissionId: " + e.getMessage());
      infoList.add(info);
      return infoList;
    }
  }

  @Override
  public List<ClaimInfoData> claimInfoForVeteran(String veteranIcn) {
    List<ClaimInfoData> veteranClaims = new ArrayList<>();
    try {
      List<ClaimEntity> claims = claimRepository.findAllByVeteranIcn(veteranIcn);
      for (ClaimEntity claim : claims) {
        ClaimInfoData info = new ClaimInfoData();
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
      log.error("Could not find claim with the given veteranIcn");
      ClaimInfoData info = new ClaimInfoData();
      info.setErrorMessage("Could not find claim with the given veteranIcn: " + e.getMessage());
      veteranClaims.add(info);
      return veteranClaims;
    }
  }

  @Override
  public List<ClaimInfoData> claimInfoWithPagination(int offset, int pageSize) {
    List<ClaimInfoData> infoList = new ArrayList<>();
    try {
      Page<ClaimEntity> entityList = claimRepository.findAll(PageRequest.of(offset, pageSize));
      for (ClaimEntity claim : entityList) {
        ClaimInfoData info = new ClaimInfoData();
        info.setVeteranIcn(claim.getVeteran().getIcn());
        info.setClaimSubmissionId(claim.getClaimSubmissionId());
        info.setContentionsCount(claim.getContentions().size());
        setContentionsList(claim, info);
        setAssessmentResultsAndCount(claim, info);
        setEvidenceSummaryCounts(claim, info);
        infoList.add(info);
      }
      return infoList;
    } catch (Exception e) {
      log.error("Error getting page of claims in claimInfoWithPagination.");
      ClaimInfoData info = new ClaimInfoData();
      info.setErrorMessage("Could not find claim with the given veteranIcn: " + e.getMessage());
      infoList.add(info);
      return infoList;
    }
  }

  private void setContentionsList(ClaimEntity entity, ClaimInfoData info) {
    List<String> diagnosticCodes = new ArrayList<>();
    for (ContentionEntity contention : entity.getContentions()) {
      diagnosticCodes.add(contention.getDiagnosticCode());
    }
    info.setContentions(diagnosticCodes);
  }

  private void setAssessmentResultsAndCount(ClaimEntity entity, ClaimInfoData info) {
    int count = 0;
    for (ContentionEntity contention : entity.getContentions()) {
      for (AssessmentResultEntity assessmentResult : contention.getAssessmentResults()) {
        info.setEvidenceSummary(assessmentResult.getEvidenceCountSummary());
        count++;
      }
    }
    info.setAssessmentResultsCount(count);
  }

  private void setEvidenceSummaryCounts(ClaimEntity claim, ClaimInfoData info) {
    int count = 0;
    for (ContentionEntity contention : claim.getContentions()) {
      count += contention.getEvidenceSummaryDocuments().size();
    }
    info.setEvidenceSummaryDocumentsCount(count);
  }
}
