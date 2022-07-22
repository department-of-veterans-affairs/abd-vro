package gov.va.vro.api.demo.model;

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
