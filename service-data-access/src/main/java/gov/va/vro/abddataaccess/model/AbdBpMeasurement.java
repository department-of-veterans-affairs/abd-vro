package gov.va.vro.abddataaccess.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AbdBpMeasurement {
  private String code;
  private String display;
  private String unit;
  private BigDecimal value;
}
