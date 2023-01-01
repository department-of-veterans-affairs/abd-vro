package gov.va.vro.model.claimmetrics;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class ClaimMetricsInfo {
  private long totalClaims;
  private long totalEvidenceGenerations;
  private long totalPdfGenerations;
}
