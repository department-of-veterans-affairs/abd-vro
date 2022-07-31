package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AbdEvidence {
  private List<AbdMedication> medications;
  private List<AbdCondition> conditions;
  private List<AbdProcedure> procedures;

  @JsonProperty("bp_readings")
  private List<AbdBloodPressure> bloodPressures;
}
