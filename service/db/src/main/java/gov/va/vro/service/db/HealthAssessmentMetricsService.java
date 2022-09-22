package gov.va.vro.service.db;

import gov.va.vro.service.spi.model.HealthAssessmentMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthAssessmentMetricsService {

  HealthAssessmentMetrics ham = new HealthAssessmentMetrics();

  public HealthAssessmentMetrics getMetrics() {
    // We need to get evidenceSummary data from FullHealthAssessment and put in DB.
    // Then add up BpReadings, RecentBpReadings, and Medications count and save totals to ham.
    // Then return ham to ClaimMetrics.


    return ham;
  }
}
