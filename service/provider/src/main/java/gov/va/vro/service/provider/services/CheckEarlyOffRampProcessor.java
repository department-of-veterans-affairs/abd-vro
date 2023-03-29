package gov.va.vro.service.provider.services;

import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.MISSING_ANCHOR;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.NEW_NOT_PRESUMPTIVE;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.OUT_OF_SCOPE;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckEarlyOffRampProcessor implements Processor {
    private final BipClaimService bipClaimService;

    // Used for logging even though this is in the mas-processing route.
    private final String sourceRoute = "/automatedClaim";
    @Override
    public void process(Exchange exchange) {
        MasProcessingObject mpo = exchange.getIn().getBody(MasProcessingObject.class);
        MasAutomatedClaimPayload payload = mpo.getClaimPayload();

        if (!payload.isInScope()) {
            exchange.setProperty("offRampError", NEW_NOT_PRESUMPTIVE);
            exchange.setProperty("sourceRoute", sourceRoute);
            var message =
                    String.format(
                            "Claim with [collection id = %s], [diagnostic code = %s],"
                                    + " and [disability action type = %s] is not in scope.",
                            payload.getCollectionId(),
                            payload.getDiagnosticCode(),
                            payload.getDisabilityActionType());
            log.info(message);
        }

        if (payload.isPresumptive() != null && !payload.isPresumptive()) {
            exchange.setProperty("offRampError", OUT_OF_SCOPE);
            exchange.setProperty("sourceRoute", sourceRoute);
            var message =
                    String.format(
                            "Claim with [collection id = %s], [diagnostic code = %s],"
                                    + " [disability action type = %s] and [flashIds = %s] is not presumptive.",
                            payload.getCollectionId(),
                            payload.getDiagnosticCode(),
                            payload.getDisabilityActionType(),
                            payload.getVeteranFlashIds());
            log.info(message);
        }

        long claimId = Long.parseLong(payload.getClaimDetail().getBenefitClaimId());
        if (!bipClaimService.hasAnchors(claimId)) {
            exchange.setProperty("offRampError", MISSING_ANCHOR);
            exchange.setProperty("sourceRoute", sourceRoute);
            var message =
                    String.format(
                            "Claim with [collection id = %s] does not qualify for"
                                    + " automated processing because it is missing anchors.",
                            payload.getCollectionId());
            log.info(message);
        }
    }
}
