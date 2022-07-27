package gov.va.vro.api.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class FetchPdfResponse {
  String claimSubmissionId;
  String status;
  public String pdfData;
}
