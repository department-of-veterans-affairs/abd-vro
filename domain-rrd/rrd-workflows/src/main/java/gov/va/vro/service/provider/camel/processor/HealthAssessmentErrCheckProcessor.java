package gov.va.vro.service.provider.camel.processor;

import gov.va.vro.model.rrd.HealthDataAssessment;
import gov.va.vro.service.provider.ExternalCallException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthAssessmentErrCheckProcessor implements Processor {
  @Override
  @SneakyThrows
  public void process(Exchange exchange) {

    HealthDataAssessment hda = exchange.getMessage().getBody(HealthDataAssessment.class);

    if (hda.getErrorMessage() != null) {
      log.error("Health Data Assessment sent back with error : {}", hda.getErrorMessage());
      throw new ExternalCallException(hda.getErrorMessage());
    }
    // Else processing should continue do not alter the exchange body or properties
  }
}
