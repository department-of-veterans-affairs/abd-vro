package gov.va.vro.controller;

import gov.va.vro.api.resources.MasResource;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.MasExamOrderStatusPayload;
import gov.va.vro.model.mas.request.MasAutomatedClaimRequest;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.mas.service.MasProcessingService;
import lombok.RequiredArgsConstructor;
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
            .dateOfBirth(request.getDateOfBirth())
            .veteranIdentifiers(request.getVeteranIdentifiers())
            .veteranFlashIds(request.getVeteranFlashIds())
            .build();

    if (!hasValidClaimId(request)) {
      throw new BipException(
          HttpStatus.BAD_REQUEST, "The request does not have a valid BenefitClaimId.");
    }
    log.info(
        "Related claim ID: {}",
        payload.getClaimDetail().getBenefitClaimId()); // TODO: remove after test.
    String message = masProcessingService.processIncomingClaim(payload);
    MasResponse response = MasResponse.builder().id(correlationId).message(message).build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MasResponse> examOrderingStatus(MasExamOrderStatusPayload payload) {
    int collectionId = payload.getCollectionId();
    log.info("Received MAS order status request with collection ID {}", collectionId);
    String correlationId = UUID.randomUUID().toString();
    payload.setCorrelationId(correlationId);
    masProcessingService.examOrderingStatus(payload);
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
