package gov.va.vro.controller;

import gov.va.vro.api.model.bip.BipUpdateClaimPayload;
import gov.va.vro.api.resources.BipResource;
import gov.va.vro.api.responses.BipClaimResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<BipClaimResponse> updateClaim(@Valid BipUpdateClaimPayload request) {
    log.info("Received BIP claim update request with claim ID {}", request.getClaimId());

    // TODO: route to call BipApiService

    BipClaimResponse response = BipClaimResponse.builder().updated(true).message("Updated").build();
    return ResponseEntity.ok(response);
  }
}