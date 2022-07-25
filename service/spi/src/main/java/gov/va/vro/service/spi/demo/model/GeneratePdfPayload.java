package gov.va.vro.service.spi.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString(includeFieldNames = true)
public class GeneratePdfPayload {
  @NonNull private String claimSubmissionId;

  @NonNull private String diagnosticCode;

  // input JSON string
  @JsonProperty("veteranInfo")
  private String veteranInfo;

  // input JSON string
  @JsonProperty("evidence")
  private String evidence;

  // output JSON string
  public String pdfDocumentJson;
}
