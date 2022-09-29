package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbdEvidence {
  private List<AbdMedication> medications;
  private List<AbdCondition> conditions;
  private List<AbdProcedure> procedures;

  @JsonProperty("bp_readings")
  private List<AbdBloodPressure> bloodPressures;
}
