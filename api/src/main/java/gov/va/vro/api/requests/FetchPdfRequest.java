package gov.va.vro.api.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Schema(name = "FetchPdfRequest", description = "Details for fetching generated pdf")
public class FetchPdfRequest {
  @NonNull
  @Schema(description = "Claim submission ID", example = "0")
  private String claimSubmissionId;

  @JsonCreator
  public FetchPdfRequest(String claimSubmissionId) {
    this.claimSubmissionId = claimSubmissionId;
  }
}
