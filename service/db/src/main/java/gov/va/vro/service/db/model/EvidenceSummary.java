package gov.va.vro.service.db.model;

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
  public int totalBpReadings;
  public int medicationsCount;
  public int recentBpReadings;
}
