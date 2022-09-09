package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FetchPdfResponse {

  @NotBlank private String claimSubmissionId;
  private String status;
  private String diagnosis;
  private String pdfData;

  public boolean hasContent() {
    return pdfData != null && pdfData.length() > 0;
  }
}
