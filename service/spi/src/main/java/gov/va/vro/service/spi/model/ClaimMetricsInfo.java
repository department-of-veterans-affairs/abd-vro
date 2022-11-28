package gov.va.vro.service.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class ClaimMetricsInfo {
  long totalClaims;
  int medicationsCount;
  int totalBpReadings;
  int recentBpReadings;
  int proceduresCount;
  String errorMessage;
}
