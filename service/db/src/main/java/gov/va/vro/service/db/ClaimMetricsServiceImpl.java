package gov.va.vro.service.db;

import gov.va.vro.persistence.repository.AssessmentResultRepository;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  private final AssessmentResultRepository assessmentResultRepository;

  @Override
  public ClaimMetricsInfo claimMetrics() {
    ClaimMetricsInfo metrics = new ClaimMetricsInfo();
    try {
      metrics.setTotalClaims(claimRepository.count());
      // metrics.setProceduresCount(assessmentResultRepository.getProceduresCount());
      if (assessmentResultRepository.count() != 0) {
        metrics.setMedicationsCount(assessmentResultRepository.getMedicationsCount());
        metrics.setTotalBpReadings(assessmentResultRepository.getTotalBpReadingsCount());
        metrics.setRecentBpReadings(assessmentResultRepository.getRecentBpReadingsCount());
      }
      return metrics;
    } catch (Exception e) {
      log.error("Could not get metrics in claim repository.", e);
      metrics.setTotalClaims(0);
      metrics.setErrorMessage("Failure;" + e.getMessage());
      return metrics;
    }
  }
}
