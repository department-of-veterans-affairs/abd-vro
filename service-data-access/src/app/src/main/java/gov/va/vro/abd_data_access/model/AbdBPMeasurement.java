package gov.va.vro.abd_data_access.model;

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
