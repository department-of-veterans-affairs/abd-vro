package gov.va.vro.abd_data_access.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AbdClaim {
    private String veteranIcn;
    private int diagnosticCode;
    private String claimSubmissionId;
}
