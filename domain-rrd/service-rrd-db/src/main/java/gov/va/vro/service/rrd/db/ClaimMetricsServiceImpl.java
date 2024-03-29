package gov.va.vro.service.rrd.db;

import gov.va.vro.model.rrd.claimmetrics.ClaimInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ClaimsInfo;
import gov.va.vro.model.rrd.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimInfoResponse;
import gov.va.vro.model.rrd.claimmetrics.response.ClaimMetricsResponse;
import gov.va.vro.model.rrd.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.persistence.model.ExamOrderEntity;
import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ClaimSubmissionRepository;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.persistence.repository.ExamOrderRepository;
import gov.va.vro.service.rrd.db.mapper.ClaimInfoResponseMapper;
import gov.va.vro.service.rrd.db.mapper.ExamOrderInfoResponseMapper;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  private final ClaimSubmissionRepository claimSubmissionRepository;

  private final AssessmentResultRepository assessmentResultRepository;
  private final EvidenceSummaryDocumentRepository evidenceSummaryDocumentRepository;

  private final ExamOrderRepository examOrderRepository;

  private final ClaimInfoResponseMapper claimInfoResponseMapper;

  private final ExamOrderInfoResponseMapper examOrderInfoResponseMapper;

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
    // v1 endpoints provide a claimSubmissionId, which maps to the reference_id on claim_submission
    // table.
    ClaimSubmissionEntity claimSubmission =
        claimSubmissionRepository
            .findFirstByReferenceIdAndIdTypeOrderByCreatedAtDesc(claimSubmissionId, idType)
            .orElse(null);
    ClaimEntity claim = null;

    if (claimSubmission != null) {
      claim = claimSubmission.getClaim();
    }
    if (claim == null) {
      // search claims by benefit claim ID if we cannot find by collection ID
      log.warn("Could not find claim with the claimSubmissionId: {}, retrying.", claimSubmissionId);
      ClaimEntity claimEntity = claimRepository.findByVbmsId(claimSubmissionId).orElse(null);
      if (claimEntity == null) {
        log.warn(
            "Could not find claim with claimSubmissionId: {}, return null.", claimSubmissionId);
        return null;
      } else {
        return claimInfoResponseMapper.toClaimInfoResponseV2(claimEntity);
      }
    }
    if (idType.equals(Claim.V1_ID_TYPE)) {
      return claimInfoResponseMapper.toClaimInfoResponseV1(claim);
    } else {
      return claimInfoResponseMapper.toClaimInfoResponseV2(claim);
    }
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
    List<ClaimInfoResponse> resp = new ArrayList<>();
    for (ClaimEntity claim : claims) {
      ClaimInfoResponse info;
      if (claim.getVbmsId() != null) {
        info = claimInfoResponseMapper.toClaimInfoResponseV2(claim);
      } else {
        info = claimInfoResponseMapper.toClaimInfoResponseV1(claim);
      }
      resp.add(info);
    }
    return new ClaimsInfo(resp, claims.getTotalElements());
  }

  private Page<ExamOrderEntity> findAllExamOrderInfoPage(ExamOrderInfoQueryParams params) {
    int size = params.getSize();
    int page = params.getPage();
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
    return examOrderRepository.findAll(pageRequest);
  }

  @Override
  public ExamOrdersInfo findExamOrderInfo(ExamOrderInfoQueryParams params) {
    List<ExamOrderInfoResponse> examOrdersInfo;
    if (params.getNotOrdered() == Boolean.TRUE) {
      List<ExamOrderEntity> examOrders = examOrderRepository.findByOrderedAtIsNull();
      examOrdersInfo = examOrderInfoResponseMapper.toExamOrderInfoResponses(examOrders);
    } else {
      Page<ExamOrderEntity> examOrders = findAllExamOrderInfoPage(params);
      examOrdersInfo = examOrderInfoResponseMapper.toExamOrderInfoResponses(examOrders);
    }
    return new ExamOrdersInfo(examOrdersInfo, examOrdersInfo.size());
  }

  @Override
  public ExamOrdersInfo findExamOrderInfoOlderThan24(ExamOrderInfoQueryParams params) {
    List<ExamOrderInfoResponse> examOrdersInfo;
    if (params.getNotOrdered() == Boolean.TRUE) {
      LocalDateTime date = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
      List<ExamOrderEntity> examOrders =
          examOrderRepository.findByCreatedAtBeforeAndOrderedAtIsNull(date);
      examOrdersInfo = examOrderInfoResponseMapper.toExamOrderInfoResponses(examOrders);
    } else {
      LocalDateTime date = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
      List<ExamOrderEntity> examOrders = examOrderRepository.findByCreatedAtBefore(date);
      examOrdersInfo = examOrderInfoResponseMapper.toExamOrderInfoResponses(examOrders);
    }
    return new ExamOrdersInfo(examOrdersInfo, examOrdersInfo.size());
  }
}
