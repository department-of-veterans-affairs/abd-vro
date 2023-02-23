package gov.va.vro.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeatureFlag {
    
    private String name;
    private String description;
    private boolean value;

}
