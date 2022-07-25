package gov.va.vro.api.demo.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
