package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdEvidence {
  @Schema(description = "List of relevant medications")
  private List<AbdMedication> medications;

  @Schema(description = "List of relevant conditions")
  private List<AbdCondition> conditions;

  @Schema(description = "List of relevant procedures")
  private List<AbdProcedure> procedures;

  @Schema(description = "List of relevant blood pressures")
  @JsonProperty("bp_readings")
  private List<AbdBloodPressure> bloodPressures;

  @Schema(description = "Veteran service locations for the pdf")
  private List<ServiceLocation> serviceLocations;

  @JsonProperty("documentsWithoutAnnotationsChecked")
  private List<String> documentsWithoutAnnotationsChecked;
}
