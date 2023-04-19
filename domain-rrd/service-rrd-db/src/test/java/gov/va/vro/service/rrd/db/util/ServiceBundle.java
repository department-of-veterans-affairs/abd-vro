package gov.va.vro.service.rrd.db.util;

import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.service.spi.db.SaveToDbService;
import gov.va.vro.service.spi.services.ClaimMetricsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceBundle {
  private ClaimMetricsService claimMetricsService;
  private SaveToDbService saveToDbService;
  private ClaimRepository claimRepository;
}
