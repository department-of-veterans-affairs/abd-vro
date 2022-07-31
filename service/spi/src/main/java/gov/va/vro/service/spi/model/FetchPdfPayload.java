package gov.va.vro.service.spi.model;

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
public class FetchPdfPayload {
  @NonNull private String claimSubmissionId;

  // output JSON string
  public String pdfDocumentJson;
}
