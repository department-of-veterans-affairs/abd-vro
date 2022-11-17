package gov.va.vro.controller;

import gov.va.vro.api.resources.BipResource;
import gov.va.vro.api.responses.*;
import gov.va.vro.model.bip.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;
import javax.validation.Valid;

/**
 * It provides all the endpoints for BIP API services.
 *
 * @author warren @Date 11/7/22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("!qa & !sandbox & !prod")
public class BipController implements BipResource {

  @Override
  public ResponseEntity<BipClaimStatusResponse> updateClaim(@Valid BipUpdateClaimPayload request) {
    log.info("Set the claim status to RFD for claim ID {}", request.getClaimId());

    // TODO: route to call BipApiService

    BipClaimStatusResponse response =
        BipClaimStatusResponse.builder().updated(true).message("Updated").build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<BipClaimResponse> getClaim(@Valid BipRequestClaimPayload request) {
    log.info("Received claim info for claim ID {}", request.getClaimId());

    // TODO: route to call BipApiService

    BipClaimResponse response =
        BipClaimResponse.builder().claimId(request.getClaimId()).message("not called").build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<BipClaimContentionsResponse> getContentions(
      @Valid BipRequestClaimContentionPayload payload) {
    log.info("Retrieve contentions for claim ID {}", payload.getClaimId());

    // TODO: route to call BipApiService

    BipClaimContentionsResponse response =
        BipClaimContentionsResponse.builder()
            .contentions(Arrays.asList(new ClaimContention()))
            .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<BipContentionUpdateResponse> updateContentions(
      @Valid BipUpdateClaimContentionPayload payload) {
    log.info("update a contention for claim ID {}", payload.getClaimId());

    // TODO: route to call BipApiService

    BipContentionUpdateResponse response =
        BipContentionUpdateResponse.builder()
            .updated(true)
            .contentionId(
                Optional.ofNullable(payload.getContention().getContentionId()).orElse(100000L))
            .message("Updated")
            .build();
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<BipContentionCreationResponse> createContentions(
      @Valid BipUpdateClaimContentionPayload payload) {
    log.info("Create a contention for claim ID {}", payload.getClaimId());

    // TODO: route to call BipApiService

    BipContentionCreationResponse response =
        BipContentionCreationResponse.builder()
            .contentionId(100000L)
            .created(true)
            .message("create a new contention")
            .build();
    return ResponseEntity.ok(response);
  }
}
