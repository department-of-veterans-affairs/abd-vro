package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VeteranIdentifiers {

  @NotBlank(message = "ICN is required")
  private String icn;

  @NotBlank(message = "SSN is required")
  private String ssn;

  @NotBlank(message = "Veteran File ID is required")
  @JsonProperty("veteranfileid")
  private String veteranFileId;

  @NotBlank(message = "EDIPN is required")
  private String edipn;

  @NotBlank(message = "Participant ID is required")
  @JsonProperty("participantid")
  private String participantId;
}
