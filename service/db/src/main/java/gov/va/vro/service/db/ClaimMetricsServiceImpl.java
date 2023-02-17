package gov.va.vro.service.db;

import gov.va.vro.model.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.claimmetrics.ClaimsInfo;
import gov.va.vro.model.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.db.mapper.ClaimInfoResponseMapper;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  private final ClaimSubmissionRepository claimSubmissionRepository;

  private final AssessmentResultRepository assessmentResultRepository;

  private final EvidenceSummaryDocumentRepository evidenceSummaryDocumentRepository;

  private final ClaimInfoResponseMapper claimInfoResponseMapper;

  @Override
  public ClaimMetricsResponse getClaimMetrics() {
    ClaimMetricsResponse metrics = new ClaimMetricsResponse();

    metrics.setTotalClaims(claimRepository.count());
    metrics.setTotalEvidenceGenerations(assessmentResultRepository.count());
    metrics.setTotalPdfGenerations(evidenceSummaryDocumentRepository.count());

    return metrics;
  }

  @Override
  public ClaimInfoResponse findClaimInfo(String claimSubmissionId, String idType) {
    // V1 calls hand us a claimSubmissionId which is equivalent to the reference_id on the
    // claim_submission table.
    ClaimSubmissionEntity claimSubmission =
        claimSubmissionRepository
            .findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(claimSubmissionId, idType)
            .orElse(null);
    ClaimEntity claim = null;

    if (claimSubmission != null) {
      claim = claimSubmission.getClaim();
    }

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
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
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
