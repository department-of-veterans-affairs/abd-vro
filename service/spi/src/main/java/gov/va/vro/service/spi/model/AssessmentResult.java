package gov.va.vro.service.spi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {

    private UUID id;
    private int evidenceCount;
    private String evidenceSummary;
}
