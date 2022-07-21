package gov.va.vro.abd_data_access.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class AbdCondition implements Comparable<AbdCondition> {
    private String text;
    private String code;
    private String abatementDate;
    private String status;
    private String onsetDate;

    @Override
    public int compareTo(AbdCondition otherCondition) {
        return StringUtils.compare(onsetDate, otherCondition.onsetDate);
    }
}
