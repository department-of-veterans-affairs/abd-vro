package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author warren @Date 11/8/22
 */
@Component
@RequiredArgsConstructor
@Data
public class BipClaimResp {
    private Claim claim;
    private String phase;
    private String phaseLastChangedDate;
    private String claimLifecycleStatus;
    private Claimant claimant;
}