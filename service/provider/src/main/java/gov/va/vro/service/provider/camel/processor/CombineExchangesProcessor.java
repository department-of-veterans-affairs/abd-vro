package gov.va.vro.service.provider.camel.processor;

import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.service.provider.mas.service.MasCollectionService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;
import java.util.Map;

public class CombineExchangesProcessor implements Processor {
  private static Exchange findOffRampExchange(List<Exchange> exchanges) {
    for (Exchange exchange : exchanges) {
      Object offRampError = exchange.getProperty("offRampError");
      if (offRampError != null) {
        return exchange;
      }
    }
    return null;
  }

  private static void populate(Exchange destination, Exchange source) {
    destination.getMessage().setBody(source.getMessage().getBody());
    for (Map.Entry<String, Object> entry : source.getProperties().entrySet()) {
      destination.setProperty(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void process(Exchange exchange) {
    List<Exchange> exchanges = (List<Exchange>) exchange.getIn().getBody();
    Exchange offRampExchange = findOffRampExchange(exchanges);
    if (offRampExchange != null) {
      populate(exchange, offRampExchange);
      return;
    }

    Exchange exchange1 = exchanges.get(0);
    Exchange exchange2 = exchanges.get(1);
    var evidence1 = exchange1.getMessage().getBody(HealthDataAssessment.class);
    var evidence2 = exchange2.getMessage().getBody(HealthDataAssessment.class);
    HealthDataAssessment assessment = MasCollectionService.combineEvidence(evidence1, evidence2);
    exchange.getMessage().setBody(assessment);
  }
}
