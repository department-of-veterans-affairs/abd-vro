package gov.va.vro.api.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class FetchPdfResponse {

  @NotBlank private String claimSubmissionId;
  private String status;
  private String diagnosis;
  private String pdfData;

  public boolean hasContent() {
    return pdfData != null && pdfData.length() > 0;
  }
}
