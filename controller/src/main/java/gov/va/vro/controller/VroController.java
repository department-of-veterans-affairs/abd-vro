package gov.va.vro.controller;

import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.db.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/vro", produces = "application/json")
public class VroController {

  private final CamelEntrance camelEntrance;

  @PostMapping("/claim")
  public ResponseEntity<Claim> submitClaim(Claim claim) {
    Claim response = camelEntrance.processClaim(claim);
    return ResponseEntity.ok(response);
  }
}
