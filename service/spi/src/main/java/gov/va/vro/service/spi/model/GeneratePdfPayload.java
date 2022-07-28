package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class GeneratePdfPayload {
  @NonNull private String claimSubmissionId;

  @NonNull private String diagnosticCode;

  @JsonProperty("veteranInfo")
  private VeteranInfo veteranInfo;

  @JsonProperty("evidence")
  private String evidence;

  // output JSON string
  public String pdfDocumentJson;
}
