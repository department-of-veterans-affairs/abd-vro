package gov.va.vro.service.provider.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.HealthAssessmentSource;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.VeteranInfo;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import gov.va.vro.model.mas.ClaimCondition;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.response.FetchPdfResponse;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
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

  public static Processor convertToMasProcessingObject() {
    return FunctionProcessor.fromFunction(
        (Function<MasAutomatedClaimPayload, MasProcessingObject>)
            masAutomatedClaimPayload -> {
              var mpo = new MasProcessingObject();
              mpo.setClaimPayload(masAutomatedClaimPayload);
              return mpo;
            });
  }

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
        (Function<MasProcessingObject, Claim>)
            payload ->
                Claim.builder()
                    .benefitClaimId(payload.getBenefitClaimId())
                    .collectionId(String.valueOf(payload.getCollectionId()))
                    .idType(payload.getIdType())
                    .diagnosticCode(payload.getDiagnosticCode())
                    .veteranIcn(payload.getVeteranIcn())
                    .build());
  }

  public static Processor convertToPdfResponse() {
    return exchange -> {
      String response = exchange.getMessage().getBody(String.class);
      var pdfResponse = new ObjectMapper().readValue(response, FetchPdfResponse.class);
      exchange.getMessage().setBody(pdfResponse);
    };
  }

  public static Processor generatePdfProcessor() {
    return FunctionProcessor.fromFunction(MasIntegrationProcessors::getGeneratePdfPayload);
  }

  private static GeneratePdfPayload getGeneratePdfPayload(MasProcessingObject transferObject) {
    MasAutomatedClaimPayload claimPayload = transferObject.getClaimPayload();
    GeneratePdfPayload generatePdfPayload = new GeneratePdfPayload();
    generatePdfPayload.setEvidence(transferObject.getEvidence());
    generatePdfPayload.setClaimSubmissionId(String.valueOf(claimPayload.getCollectionId()));
    generatePdfPayload.setIdType(transferObject.getIdType());
    generatePdfPayload.setPdfTemplate("v2");
    generatePdfPayload.setDiagnosticCode(
        claimPayload.getClaimDetail().getConditions().getDiagnosticCode());
    VeteranInfo veteranInfo = new VeteranInfo();
    veteranInfo.setFirst(claimPayload.getFirstName());
    veteranInfo.setLast(claimPayload.getLastName());
    veteranInfo.setMiddle("");
    veteranInfo.setBirthdate(claimPayload.getDateOfBirth());
    String fileId = claimPayload.getVeteranIdentifiers().getVeteranFileId();
    generatePdfPayload.setVeteranFileId(fileId);
    generatePdfPayload.setVeteranInfo(veteranInfo);

    String disabilityActionType = transferObject.getDisabilityActionType();
    if (disabilityActionType != null) {
      ClaimCondition condition = new ClaimCondition();
      condition.setDisabilityActionType(disabilityActionType);
      String conditionName = transferObject.getConditionName();
      if (conditionName == null) {
        conditionName = "Not Available";
      }
      condition.setName(conditionName);
      generatePdfPayload.setConditions(condition);
    }

    log.info(
        "Generating pdf for claim: {} and diagnostic code {}",
        generatePdfPayload.getClaimSubmissionId(),
        generatePdfPayload.getDiagnosticCode());
    return generatePdfPayload;
  }

  public static Processor auditProcessor(String routeId, String message) {
    return exchange -> {
      var auditable = exchange.getMessage().getBody(Auditable.class);
      exchange.getIn().setBody(AuditEvent.fromAuditable(auditable, routeId, message));
    };
  }

  public static Processor auditProcessor(
      String routeId, Function<Auditable, String> messageExtractor) {
    return exchange -> {
      var auditable = exchange.getMessage().getBody(Auditable.class);
      String message = messageExtractor.apply(auditable);
      exchange.getIn().setBody(AuditEvent.fromAuditable(auditable, routeId, message));
    };
  }

  public static Processor slackEventProcessor(String routeId, String message) {
    return exchange -> {
      MasProcessingObject masProcessingObject =
          exchange.getMessage().getBody(MasProcessingObject.class);
      String msg = message;
      if (masProcessingObject != null) {
        msg += " collection ID: " + masProcessingObject.getCollectionId();
      }
      var auditable = exchange.getMessage().getBody(Auditable.class);
      exchange.getIn().setBody(AuditEvent.fromAuditable(auditable, routeId, msg));
    };
  }

  public static Processor lighthouseContinueProcessor() {
    return exchange -> {
      HealthDataAssessment hda = new HealthDataAssessment();
      hda.setSource(HealthAssessmentSource.LIGHTHOUSE);
      exchange.getMessage().setBody(hda);
    };
  }
}
