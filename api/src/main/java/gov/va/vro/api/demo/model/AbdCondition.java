package gov.va.vro.api.demo.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

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
