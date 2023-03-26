package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdBpMeasurement {
  @Schema(description = "Code for measurement type", example = "8480-6")
  private String code;

  @Schema(description = "Display name for measurement type", example = "Systolic blood pressure")
  private String display;

  @Schema(description = "Unit for the measurement", example = "mm[Hg]")
  private String unit;

  @Schema(description = "Measurement value", example = "110")
  private BigDecimal value;
}
