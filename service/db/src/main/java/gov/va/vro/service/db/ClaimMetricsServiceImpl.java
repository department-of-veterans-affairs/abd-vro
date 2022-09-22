package gov.va.vro.service.db;

import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.model.ClaimMetricsInfo;
import gov.va.vro.service.spi.model.HealthAssessmentMetrics;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  private final HealthAssessmentMetricsService hamService;

  @Override
  public ClaimMetricsInfo claimMetrics() {
    ClaimMetricsInfo metrics = new ClaimMetricsInfo();
    HealthAssessmentMetrics ham = new HealthAssessmentMetrics();
    ham = hamService.getMetrics();
    try {
      metrics.setTotalClaims(claimRepository.count());
      // metrics.setMedicationsCount(ham.get);
      // metrics.setRecentBpReadings(ham.get);
      // metrics.setTotalBpReadings(ham.get);
      return metrics;
    } catch (Exception e) {
      log.error("Could not get metrics in claim repository.", e);
      metrics.setTotalClaims(0);
      metrics.setErrorMessage("Failure;" + e.getMessage());
      return metrics;
    }
  }
}
