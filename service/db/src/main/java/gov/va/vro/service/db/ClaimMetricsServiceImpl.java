package gov.va.vro.service.db;

import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsServiceImpl implements ClaimMetricsService {

  private final ClaimRepository claimRepository;

  @Override
  public Integer claimMetrics() {
    try {
      List claims = claimRepository.findAll();
      Integer total = claims.size();
      return total;
    } catch (Exception e) {
      log.error("Couldnt findAll in claim repository.", e);
      Integer total = null;
      return total;
    }
  }
}
