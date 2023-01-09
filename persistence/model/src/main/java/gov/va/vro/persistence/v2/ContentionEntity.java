package gov.va.vro.persistence.v2;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contention")
public class ContentionEntity extends BaseEntity {


    public enum RequestType {
        CLAIM_SUBMISSION,
        AUTOMATED_CLAIM,
        OTHER;
    }

    public enum Status {
        PROCESSED,
        REJECTED,
        ERROR;
    }

    @ManyToOne
    private ClaimEntity claim;

    @NotNull
    private String diagnosticCode;

    private RequestType requestType;
    private Status status;

    public ContentionEntity(String diagnosticCode) {
        this.diagnosticCode = diagnosticCode;
    }

}
