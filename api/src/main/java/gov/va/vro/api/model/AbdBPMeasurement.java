package gov.va.vro.api.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AbdBPMeasurement {
  private String code;
  private String display;
  private String unit;
  private BigDecimal value;
}
