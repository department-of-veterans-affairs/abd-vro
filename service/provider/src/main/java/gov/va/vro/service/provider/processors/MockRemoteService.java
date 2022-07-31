package gov.va.vro.service.provider.processors;

import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This service is a placeholder for remote services to be connected to the camel routes. It should
 * be deleted once the mock routes are replaced with rabbitmq routes
 */
@RequiredArgsConstructor
@Slf4j
public class MockRemoteService {

  private final String name;

  public Claim processClaim(Claim claim) {
    log.info("Service {} received claim {}", name, claim);
    return claim;
  }
}
