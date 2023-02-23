package gov.va.vro.service.provider;

import gov.va.vro.service.provider.mas.MasException;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasAccessErrProcessor implements Processor {

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    var claimPayload = exchange.getMessage().getBody(MasProcessingObject.class);

    log.info("Off Ramp : Sufficiency can't be determined {}.", claimPayload.getCollectionId());
    try {
      exchange.setProperty("OffRampAccessError", "OffRampSufficiencyNull");
    } catch (MasException e) {
      log.error("Error in calling MasAccessErrProcessor ", e);
      throw new MasException("Error in MasAccessErrProcessor ", e);
    }
  }
}
