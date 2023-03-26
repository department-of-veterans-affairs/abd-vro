package gov.va.vro.model.rrd.claimmetrics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class ClaimMetricsResponse {
  private long totalClaims;
  private long totalEvidenceGenerations;
  private long totalPdfGenerations;
}
