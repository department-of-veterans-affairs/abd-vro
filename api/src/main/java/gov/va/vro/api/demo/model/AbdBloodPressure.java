package gov.va.vro.api.demo.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

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
