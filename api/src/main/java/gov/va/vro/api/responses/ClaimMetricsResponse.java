package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ClaimMetricsResponse {
  private long numberOfClaims;
  private int medicationsCount;
  private int totalBpReadings;
  private int recentBpReadings;
  private int proceduresCount;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;
}
