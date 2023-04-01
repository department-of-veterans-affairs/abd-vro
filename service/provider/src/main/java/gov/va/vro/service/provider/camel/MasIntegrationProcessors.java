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
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipClaimService;
import gov.va.vro.service.provider.bip.service.BipUpdateClaimResult;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasCompletionStatus;
import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
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
            payload -> new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING));
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

  public static Processor setOffRampReasonProcessor(String offRampReason) {
    return exchange -> {
      MasProcessingObject mpoOfframp = exchange.getMessage().getBody(MasProcessingObject.class);
      mpoOfframp.getClaimPayload().setOffRampReason(offRampReason);
      exchange.getMessage().setBody(mpoOfframp);
    };
  }

  /**
   * At the conclusion of automated claim processing this processor updates claims and contentions
   * using BIP Claims API.
   *
   * @param bipClaimService
   * @return Processor completion camel processor
   */
  public static Processor completionProcessor(
      BipClaimService bipClaimService, MasProcessingService masProcessingService) {
    return exchange -> {
      MasProcessingObject payload = exchange.getIn().getBody(MasProcessingObject.class);

      //String offRampError = payload.getClaimPayload().getOffRampError();
      MasCamelStage origin = payload.getOrigin();
      Boolean sufficient = exchange.getProperty("sufficientForFastTracking", Boolean.class);
      String offRampErrorPayload = payload.getOffRampReason();

      // Update our database with offramp reason.
      if (offRampErrorPayload != null) {
        masProcessingService.offRampClaimForError(payload, offRampErrorPayload);
        exchange.setProperty("completionSlackMessage", offRampErrorPayload);
      }
      //MasCompletionStatus completionStatus = MasCompletionStatus.of(payload);
      MasCompletionStatus completionStatus =
          MasCompletionStatus.of(origin, sufficient, offRampErrorPayload);
      try {
        BipUpdateClaimResult result = bipClaimService.updateClaim(payload, completionStatus);
        if (result.hasMessage()) {
          exchange.setProperty("completionSlackMessage", result.getMessage());
        }
      } catch (BipException exception) {
        log.error("Error using BIP Claims API", exception);
        String message = "BIP Claims API exception: " + exception.getMessage();
        exchange.setProperty("completionSlackMessage", message);
      }
    };
  }

  public static Processor slackEventProcessor(String routeId, String message) {
    return exchange -> {
      MasProcessingObject masProcessingObject =
          exchange.getMessage().getBody(MasProcessingObject.class);
      exchange
          .getIn()
          .setBody(
              AuditEvent.fromAuditable(
                  masProcessingObject, routeId, getSlackMessage(masProcessingObject, message)));
    };
  }

  public static Processor slackEventPropertyProcessor(String routeId, String exchangeProperty) {
    return exchange -> {
      String message = exchange.getProperty(exchangeProperty, "Unidentified message", String.class);
      MasProcessingObject masProcessingObject =
          exchange.getMessage().getBody(MasProcessingObject.class);
      exchange
          .getIn()
          .setBody(
              AuditEvent.fromAuditable(
                  masProcessingObject, routeId, getSlackMessage(masProcessingObject, message)));
    };
  }

  // Used for inline grabbing of errors that need to go to audit and slack, but the
  // MasProcessingObject was stored
  // not in the body at that point in the code.
  public static Processor slackEventPropertyProcessor(
      String routeId, String message, String exchangeProperty) {
    return exchange -> {
      MasProcessingObject masProcessingObject =
          exchange.getProperty(exchangeProperty, MasProcessingObject.class);
      exchange
          .getIn()
          .setBody(
              AuditEvent.fromAuditable(
                  masProcessingObject, routeId, getSlackMessage(masProcessingObject, message)));
    };
  }

  public static String getSlackMessage(MasProcessingObject mpo, String originalMessage) {
    String msg = originalMessage;
    if (mpo != null) {
      msg += " collection ID: " + mpo.getCollectionId();
    }
    return msg;
  }

  /**
   * This sets up a skeleton lighthouse HealthDataAsessment object in the event of a timeout from
   * lighthouse. We know the fields we MUST have for evidence merge from the properties we saved on
   * the exchange.
   *
   * <p>We do this because of the request here
   * https://github.com/department-of-veterans-affairs/abd-vro/issues/1314 That asks us to continue
   * processing as if nothing has gone wrong in this case other than notifying slack.
   *
   * @return
   */
  public static Processor lighthouseContinueProcessor() {
    return exchange -> {
      MasProcessingObject mpo = exchange.getProperty("payload", MasProcessingObject.class);
      HealthDataAssessment hda = new HealthDataAssessment();
      hda.setSource(HealthAssessmentSource.LIGHTHOUSE);
      hda.setDiagnosticCode(mpo.getDiagnosticCode());
      hda.setClaimSubmissionId(Integer.toString(mpo.getCollectionId()));
      hda.setVeteranIcn(mpo.getVeteranIcn());
      exchange.getMessage().setBody(hda);
    };
  }
}
