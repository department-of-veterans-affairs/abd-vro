package gov.va.vro.api.responses;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class FetchPdfResponse {

  @NotBlank private String claimSubmissionId;
  private String status;
  private String pdfData;

  public boolean hasContent() {
    return pdfData != null && pdfData.length() > 0;
  }
}
