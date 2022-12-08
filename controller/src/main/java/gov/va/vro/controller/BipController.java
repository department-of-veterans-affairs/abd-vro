package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.resources.BipResource;
import gov.va.vro.api.responses.*;
import gov.va.vro.model.bip.*;
import gov.va.vro.service.provider.bip.BipException;
import gov.va.vro.service.provider.bip.service.BipApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
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
  private final BipApiService service;

  @Override
  public ResponseEntity<BipClaimStatusResponse> setClaimRFD(@Valid BipUpdateClaimPayload request) {
    log.info("Set the claim status to RFD for claim ID {}", request.getClaimId());

    // TODO: route to call BipApiService
    long claimId = Long.parseLong(request.getClaimId());
    try {
      BipUpdateClaimResp resp = service.updateClaimStatus(claimId);
      String msg = resp.getMessage();
      boolean isSuccessful = resp.getStatus() == HttpStatus.OK;
      BipClaimStatusResponse response =
          BipClaimStatusResponse.builder().updated(isSuccessful).message(msg).build();
      return ResponseEntity.ok(response);
    } catch (BipException e) {
      log.error("failed to update claim to RFD, {}", claimId);
      BipClaimStatusResponse badResp =
          BipClaimStatusResponse.builder().updated(false).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipClaimResponse> getClaim(@Valid String id) {
    try {
      long claimId = Long.parseLong(id);
      log.info("Received claim info for claim ID {}", claimId);

      // TODO: route to call BipApiService

      BipClaim response = service.getClaimDetails(claimId);
      BipClaimResponse resp = BipClaimResponse.builder().claimId(claimId).claim(response).build();
      return ResponseEntity.ok(resp);
    } catch (SecurityException | NullPointerException e) {
      BipClaimResponse errResp =
          BipClaimResponse.builder().message("Invalid claim ID: " + id).build();
      return ResponseEntity.badRequest().body(errResp);
    } catch (BipException e) {
      log.error("failed to get claim details, {}", id, e);
      BipClaimResponse bipErrResp = BipClaimResponse.builder().message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(bipErrResp);
    }
  }

  @Override
  public ResponseEntity<BipClaimContentionsResponse> getContentions(@Valid String id) {
    long claimId = Long.parseLong(id);
    log.info("Retrieve contentions for claim ID {} from {}", claimId, id);
    try {
      // TODO: route to call BipApiService

      List<ClaimContention> resp = service.getClaimContentions(claimId);
      BipClaimContentionsResponse response =
          BipClaimContentionsResponse.builder().contentions(resp).claimId(claimId).build();
      return ResponseEntity.ok(response);
    } catch (SecurityException | NullPointerException e) {
      return ResponseEntity.badRequest().build();
    } catch (BipException e) {
      log.error("failed to get contentions for {}", id, e);
      BipClaimContentionsResponse badResp =
          BipClaimContentionsResponse.builder().claimId(claimId).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipContentionUpdateResponse> updateContentions(
      @Valid BipUpdateClaimContentionPayload payload) {
    log.info("update a contention for claim ID {}", payload.getClaimId());
    log.info("contention to update:\n {}", payload.getContention());

    // TODO: route to call BipApiService

    try {
      UpdateContention contention = payload.getContention();
      List<UpdateContention> contentions = Collections.singletonList(contention);
      UpdateContentionReq req = new UpdateContentionReq();
      req.setUpdateContentions(contentions);
      BipUpdateClaimResp resp = service.updateClaimContention(payload.getClaimId(), req);
      boolean isUpdated = resp.getStatus() == HttpStatus.OK;
      BipContentionUpdateResponse response =
          BipContentionUpdateResponse.builder()
              .updated(isUpdated)
              .contentionId(Optional.of(payload.getContention().getContentionId()).orElse(0L))
              .message(resp.getMessage())
              .build();
      return ResponseEntity.ok(response);
    } catch (BipException e) {
      BipContentionUpdateResponse badResp =
          BipContentionUpdateResponse.builder().updated(false).message(e.getMessage()).build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }

  @Override
  public ResponseEntity<BipContentionCreationResponse> createContentions(
      @Valid BipCreateClaimContentionPayload payload) {
    log.info("Create a contention for claim ID {}", payload.getClaimId());
    log.info("contention to create: \n {}", payload.getContention());

    // TODO: route to call BipApiService
    try {
      List<CreateContention> contentions = Collections.singletonList(payload.getContention());
      CreateContentionReq req = new CreateContentionReq();
      req.setCreateContentions(contentions);
      BipUpdateClaimResp resp = service.addClaimContention(payload.getClaimId(), req);
      boolean isCreated = resp.getStatus() == HttpStatus.CREATED;
      //TODO: If isCreated, return the contention content.
      BipContentionCreationResponse response =
          BipContentionCreationResponse.builder()
              .claimId(payload.getClaimId())
              .created(isCreated)
              .message(resp.getMessage())
              .build();
      return ResponseEntity.ok(response);
    } catch (BipException e) {
      log.error("failed to create contention for claim {}", payload.getClaimId(), e);
      BipContentionCreationResponse badResp =
          BipContentionCreationResponse.builder()
              .contentionId(payload.getClaimId())
              .created(false)
              .message(e.getMessage())
              .build();
      return ResponseEntity.internalServerError().body(badResp);
    }
  }
}