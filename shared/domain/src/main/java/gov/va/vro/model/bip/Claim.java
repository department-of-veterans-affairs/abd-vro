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
public class Claim {
    private String summaryDateTime;
    private String lastModified;
    private String claimId;
    private BenefitClaimType benefitClaimType;

}