package gov.va.vro.api.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbdEvidence {
    private List<AbdMedication> medications;
    private List<AbdCondition> conditions;
    private List<AbdProcedure> procedures;

    @JsonProperty("bp_readings")
    private List<AbdBloodPressure> bloodPressures;
}