package gov.va.vro.model.claimmetrics.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class ClaimMetricsResponse {
  private long totalClaims;
  private long totalEvidenceGenerations;
  private long totalPdfGenerations;
}
