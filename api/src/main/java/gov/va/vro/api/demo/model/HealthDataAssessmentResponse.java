package gov.va.vro.api.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

@Getter
@Setter
public class HealthDataAssessmentResponse {
    @NonNull
    @Schema(
        description = "Veteran medical internal control number (EHR id)",
        example = "90653535"
    )
    private String veteranIcn;

    @Schema(
        description = "Diagnostic code for the claim contention",
        example = "7101"
    )
    private int diagnosticCode;

    @Schema(
        description = "Medical evidence supporting assessment"
    )
    private AbdEvidence evidence;

    @Schema(
        description = "Error message in the case of an error"
    )    
    private String errorMessage;
    
    public HealthDataAssessmentResponse() {
    }

    public HealthDataAssessmentResponse(AbdClaim claim) {
        veteranIcn = claim.getVeteranIcn();
        diagnosticCode = claim.getDiagnosticCode();
    }

    public HealthDataAssessmentResponse(AbdClaim claim, AbdEvidence evidence) {
        veteranIcn = claim.getVeteranIcn();
        diagnosticCode = claim.getDiagnosticCode();
        this.evidence = evidence;
    }

    public HealthDataAssessmentResponse(AbdClaim claim, String errorMessage) {
        veteranIcn = claim.getVeteranIcn();
        diagnosticCode = claim.getDiagnosticCode();
        this.errorMessage = errorMessage;        
    }
}
