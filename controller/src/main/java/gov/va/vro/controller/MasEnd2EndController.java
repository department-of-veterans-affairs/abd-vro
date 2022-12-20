package gov.va.vro.controller;

import gov.va.vro.api.resources.MasEnd2EndResource;
import gov.va.vro.api.responses.MasResponse;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.CamelEntrance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

// @Profile("dimitri")
@RestController
@RequiredArgsConstructor
public class MasEnd2EndController implements MasEnd2EndResource {

  private final CamelEntrance camelEntrance;

  @Override
  public ResponseEntity<MasResponse> processAutomatedClaim(MasAutomatedClaimPayload payload) {
    String message = camelEntrance.processClaim(payload);
    MasResponse response =
        MasResponse.builder()
            .id(Integer.toString(payload.getCollectionId()))
            .message(message)
            .build();
    return ResponseEntity.ok(response);
  }
}
