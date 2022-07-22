package gov.va.vro.api.demo.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class AbdMedication implements Comparable<AbdMedication> {
    private String text;
    private String code;
    private String date;
    private String status;
    private List<String> notes;
    private int refills;
    private String duration;

    @Override
    public int compareTo(AbdMedication otherMedication) {
        return StringUtils.compare(date, otherMedication.date);
    }
}
