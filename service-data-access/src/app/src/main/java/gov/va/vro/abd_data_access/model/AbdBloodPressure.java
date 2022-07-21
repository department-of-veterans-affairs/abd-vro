package gov.va.vro.abd_data_access.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class AbdBloodPressure implements Comparable<AbdBloodPressure> {
    private String date;
    private AbdBPMeasurement diastolic;
    private AbdBPMeasurement systolic;
    private String practitioner;
    private String organization;

    @Override
    public int compareTo(AbdBloodPressure otherBp) {
        return StringUtils.compare(date, otherBp.date);
    }
}
