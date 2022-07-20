package gov.va.vro.service.provider.processors;

import gov.va.vro.service.spi.db.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MockRemoteService {

  private final String name;

  public Claim processClaim(Claim claim) {
    log.info("Service {} received claim {}", name, claim);
    return claim;
  }
}
