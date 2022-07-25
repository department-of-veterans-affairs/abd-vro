package gov.va.vro.abd_data_access.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

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
