package gov.va.vro.service.provider.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.mas.service.MasTransferObject;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;
import java.util.function.Function;

/** Helper processors for Mas Integration. */
@Slf4j
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

  /**
   * Processor that turns payload to claim.
   *
   * @return return
   */
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

  public static Processor covertToPdfReponse() {
    return exchange -> {
      String response = exchange.getMessage().getBody(String.class);
      var pdfResponse = new ObjectMapper().readValue(response, FetchPdfResponse.class);
      exchange.getMessage().setBody(pdfResponse);
    };
  }

  public static Processor generatePdfProcessor() {
    return FunctionProcessor.fromFunction(MasIntegrationProcessors::getGeneratePdfPayload);
  }

  private static GeneratePdfPayload getGeneratePdfPayload(MasTransferObject transferObject) {
    MasAutomatedClaimPayload claimPayload = transferObject.getClaimPayload();
    GeneratePdfPayload generatePdfPayload = new GeneratePdfPayload();
    generatePdfPayload.setEvidence(transferObject.getEvidence());
    generatePdfPayload.setClaimSubmissionId(claimPayload.getClaimDetail().getBenefitClaimId());
    generatePdfPayload.setDiagnosticCode(
        claimPayload.getClaimDetail().getConditions().getDiagnosticCode());
    VeteranInfo veteranInfo = new VeteranInfo();
    veteranInfo.setFirst(claimPayload.getFirstName());
    veteranInfo.setLast(claimPayload.getLastName());
    veteranInfo.setMiddle("");
    veteranInfo.setBirthdate(claimPayload.getDateOfBirth());
    generatePdfPayload.setVeteranInfo(veteranInfo);
    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        generatePdfPayload.getClaimSubmissionId(),
        generatePdfPayload.getDiagnosticCode());
    return generatePdfPayload;
  }
}
