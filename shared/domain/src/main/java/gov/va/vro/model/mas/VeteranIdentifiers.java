package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VeteranIdentifiers {

  @NotBlank(message = "ICN is required")
  @Schema(description = "Veteran ICN", example = "mock1012666073V986297")
  private String icn;

  @Schema(description = "Social Security Number", example = "111-11-1111")
  @NotBlank(message = "SSN is required")
  private String ssn;

  @NotBlank(message = "Veteran File ID is required")
  private String veteranFileId;

  @NotBlank(message = "EDIPN is required")
  private String edipn;

  @NotBlank(message = "Participant ID is required")
  private String participantId;
}
