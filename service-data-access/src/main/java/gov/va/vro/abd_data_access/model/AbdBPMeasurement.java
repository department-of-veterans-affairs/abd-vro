package gov.va.vro.abd_data_access.model;

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
