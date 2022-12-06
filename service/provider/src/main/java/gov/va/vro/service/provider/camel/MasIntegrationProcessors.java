package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.spi.model.Claim;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;
import java.util.function.Function;

/** Helper processors for Mas Integration */
public class MasIntegrationProcessors {

  public static Processor combineExchangesProcessor() {
    return FunctionProcessor.fromFunction(combineExchangesFunction());
  }

  private static Function<List<Exchange>, HealthDataAssessment> combineExchangesFunction() {
    return exchanges -> {
      for (Exchange exchange : exchanges) {
        if (exchange.isFailed()) {
          throw new MasException(
              "Failed to collect evidence", exchange.getException(Throwable.class));
        }
      }
      Exchange exchange1 = exchanges.get(0);
      Exchange exchange2 = exchanges.get(1);
      var evidence1 = exchange1.getMessage().getBody(HealthDataAssessment.class);
      var evidence2 = exchange2.getMessage().getBody(HealthDataAssessment.class);
      return MasCollectionService.combineEvidence(evidence1, evidence2);
    };
  }

  public static Processor payloadToClaimProcessor() {
    return FunctionProcessor.fromFunction(
        (Function<MasAutomatedClaimPayload, Claim>)
            payload ->
                Claim.builder()
                    .claimSubmissionId(payload.getClaimDetail().getBenefitClaimId())
                    .diagnosticCode(payload.getClaimDetail().getConditions().getDiagnosticCode())
                    .veteranIcn(payload.getVeteranIdentifiers().getIcn())
                    .build());
  }
}
