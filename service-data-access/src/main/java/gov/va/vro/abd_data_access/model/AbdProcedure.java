package gov.va.vro.abd_data_access.model;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class AbdProcedure implements Comparable<AbdProcedure> {
    private String text;
    private String code;
    private String status;
    private String performedDate;
    private String codeSystem;

    @Override
    public int compareTo(AbdProcedure otherProcedure) {
        return StringUtils.compare(performedDate, otherProcedure.performedDate);
    }
}
