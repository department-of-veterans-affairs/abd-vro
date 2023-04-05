package gov.va.vro.service.provider;

import gov.va.vro.model.claimmetrics.ExamOrdersInfo;
import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Used to programmatically inject messages into a Camel endpoint. AKA an entrance ramp onto a Camel
 * route. Intended to be used by Controller classes to initiate routing requests.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CamelEntrance {

  private final ProducerTemplate producerTemplate;

  public String submitClaimFull(Claim claim) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_SUBMIT_CLAIM_FULL, claim, String.class);
  }

  public String generatePdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_GENERATE_PDF, resource, String.class);
  }

  public String fetchPdf(String claimSubmissionId) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_FETCH_PDF, claimSubmissionId, String.class);
  }

  public String immediatePdf(GeneratePdfPayload resource) {
    return producerTemplate.requestBody(
        PrimaryRoutes.ENDPOINT_GENERATE_FETCH_PDF, resource, String.class);
  }

  /**
   * Notify automated claim.
   *
   * @param payload mas payload
   * @param delay delay time
   * @param retryCount amount of retries
   */
  public void notifyAutomatedClaim(MasAutomatedClaimPayload payload, long delay, int retryCount) {
    log.info(
        "Notify automated claim for claim ID: {}, collection ID: {}, icn: {}",
        payload.getBenefitClaimId(),
        payload.getCollectionId(),
        payload.getVeteranIcn());
    producerTemplate.sendBodyAndHeaders(
        MasIntegrationRoutes.ENDPOINT_AUTOMATED_CLAIM,
        payload,
        Map.of(
            MasIntegrationRoutes.MAS_DELAY_PARAM,
            delay,
            MasIntegrationRoutes.MAS_RETRY_PARAM,
            retryCount));
  }

  public void examOrderingStatus(MasExamOrderStatusPayload payload) {
    producerTemplate.requestBody(MasIntegrationRoutes.ENDPOINT_EXAM_ORDER_STATUS, payload);
  }

  /**
   * Main entry point for processing an automated MAS claim
   *
   * @param masAutomatedClaimPayload the original payload
   */
  public MasProcessingObject processClaim(MasAutomatedClaimPayload masAutomatedClaimPayload) {
    return producerTemplate.requestBody(
        MasIntegrationRoutes.ENDPOINT_MAS_PROCESSING,
        masAutomatedClaimPayload,
        MasProcessingObject.class);
  }

  /**
   * Last processing step for a MAS claim whether it has been accepted or off-ramped
   *
   * @param masProcessingObject the complete processing object
   */
  public void completeProcessing(MasProcessingObject masProcessingObject) {
    producerTemplate.requestBody(MasIntegrationRoutes.ENDPOINT_MAS_COMPLETE, masProcessingObject);
  }

  /**
   * Send appropriate notifications when a claim is off-ramped
   *
   * @param auditEvent the event to broadcast
   */
  public void offrampClaim(AuditEvent auditEvent) {
    producerTemplate.sendBody(MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT, auditEvent);
  }

  public void examOrderSlack(ExamOrdersInfo examOrders) {
    producerTemplate.sendBody(MasIntegrationRoutes.ENDPOINT_EXAM_ORDER_SLACK, examOrders);
  }
}
