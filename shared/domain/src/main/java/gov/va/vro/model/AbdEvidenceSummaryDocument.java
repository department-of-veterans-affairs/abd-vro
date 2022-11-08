package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdEvidenceSummaryDocument {

  @Schema(description = "The id for this claim submission.")
  private String claimSubmissionId;

  @Schema(description = "Status of the fetch.")
  private String status;

  @Schema(description = "The diagnosis")
  private String diagnosis;

  @Schema(description = "PDF Data")
  private String pdfData;
}
