package gov.va.vro.service.db;

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

  @Override
  public ClaimMetricsInfo claimMetrics() {
    ClaimMetricsInfo metrics = new ClaimMetricsInfo();
    try {
      metrics.setTotalClaims(claimRepository.count());
      return metrics;
    } catch (Exception e) {
      log.error("Could not get metrics in claim repository.", e);
      metrics.setTotalClaims(null);
      metrics.setErrorMessage("Failure;" + e.getMessage());
      return metrics;
    }
  }
}
