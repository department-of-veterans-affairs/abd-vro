package gov.va.vro.controller.rrd;

import gov.va.vro.api.rrd.resources.MasResource;
import gov.va.vro.api.rrd.responses.MasResponse;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.rrd.mas.MasExamOrderStatusPayload;
import gov.va.vro.model.rrd.mas.request.MasAutomatedClaimRequest;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MasController implements MasResource {

  private final MasProcessingService masProcessingService;

  /** Initiate MAS integration. */
  @SneakyThrows
  @Override
  public ResponseEntity<MasResponse> automatedClaim(MasAutomatedClaimRequest request) {
    log.info(
        "Received MAS automated claim request with collection ID {}", request.getCollectionId());
    String correlationId = UUID.randomUUID().toString();
    var payload =
        MasAutomatedClaimPayload.builder()
            .claimDetail(request.getClaimDetail())
            .collectionId(request.getCollectionId())
            .correlationId(correlationId)
            .firstName(request.getFirstName())
            .gender(request.getGender())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth().replaceAll("Z", ""))
            .veteranIdentifiers(request.getVeteranIdentifiers())
            .veteranFlashIds(request.getVeteranFlashIds())
            .build();

    if (!hasValidClaimId(request)) {
      throw new BipException(
          HttpStatus.BAD_REQUEST, "The request does not have a valid BenefitClaimId.");
    }
    log.info(
        "MAS collection related claim ID: {}, veteranId (icn): {}",
        payload.getBenefitClaimId(),
        payload.getVeteranIcn()); // TODO: remove after test.
    masProcessingService.processIncomingClaimSaveToDB(payload);
    // Any reason here will return a 422
    String message = masProcessingService.processIncomingClaimGetUnprocessableReason(payload);
    if (message != null) {
      throw new ClaimProcessingException(
          payload.getBenefitClaimId(), HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
    // Only condition in which we will off ramp message or return a valid message
    message = masProcessingService.processIncomingClaimPresumptiveOffRampClaimCheck(payload);
    MasResponse response = MasResponse.builder().id(correlationId).message(message).build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    int collectionId = payload.getCollectionId();
    log.info("Received MAS order status request with collection ID {}", collectionId);
    String correlationId = UUID.randomUUID().toString();
    payload.setCorrelationId(correlationId);
    masProcessingService.examOrderingStatus(payload, MasAutomatedClaimPayload.CLAIM_V2_ID_TYPE);
    String message =
        String.format("Received Exam Order Status for collection Id %d.", collectionId);
    MasResponse response = MasResponse.builder().id(correlationId).message(message).build();
    return ResponseEntity.ok(response);
  }

  // TODO: Add this test method for now. It'd better add some logic in MasAutomatedClaimRequest.
  private boolean hasValidClaimId(MasAutomatedClaimRequest request) {
    try {
      String claimId = request.getClaimDetail().getBenefitClaimId();
      log.info("claim ID to check: {}", claimId); // TODO: remove after test.
      long validId = Long.parseLong(claimId);
      return validId > 0L;
    } catch (Exception e) {
      return false;
    }
  }
}
