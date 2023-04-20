package gov.va.vro.service.provider;

import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasPollingProcessor implements Processor {
  private final CamelEntrance camelEntrance;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {
    var claimPayload = exchange.getMessage().getBody(MasAutomatedClaimPayload.class);
    camelEntrance.processClaim(claimPayload);
  }
}
