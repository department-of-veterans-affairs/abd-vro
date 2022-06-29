package gov.va.vro.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContentionEntity extends BaseEntity {

    @ManyToOne
    private ClaimEntity claim;

    private String diagnosticCode;

    public ContentionEntity(String diagnosticCode) {
        this.diagnosticCode = diagnosticCode;
    }
}
