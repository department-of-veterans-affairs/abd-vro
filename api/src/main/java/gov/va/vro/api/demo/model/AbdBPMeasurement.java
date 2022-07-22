package gov.va.vro.api.demo.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbdBPMeasurement {
    private String code;
    private String display;
    private String unit;
    private BigDecimal value;
}
