package gov.va.vro.service.spi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EvidenceSummary {

  private int totalBpReadings;
  private int medicationsCount;
  private int recentBpReadings;
}
