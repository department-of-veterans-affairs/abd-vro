package gov.va.vro.service.provider.services;

import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.provider.mas.MasException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimMetricsCamelService {

  private final CamelEntrance camelEntrance;

  public void examOrderSlack(ExamOrdersInfo examOrders) {
    try {
      camelEntrance.examOrderSlack(examOrders);
    } catch (Exception e) {
      throw new MasException("Could not slack exam orders.");
    }
  }
}
