package gov.va.vro.controller;

import gov.va.vro.api.resources.IntegrationTestResource;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.CamelEntrance;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

@Profile("!qa & !sandbox & !prod")
@RestController
@RequiredArgsConstructor
public class IntegrationTestController implements IntegrationTestResource {

  private final CamelEntrance camelEntrance;

  @Override
  public void automatedClaimSync(@RequestBody MasAutomatedClaimPayload payload) {
    String response = camelEntrance.processClaim(payload);
    // System.out.println(response);
  }
}
