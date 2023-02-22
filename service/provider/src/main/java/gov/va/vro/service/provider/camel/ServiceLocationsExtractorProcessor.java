package gov.va.vro.service.provider.camel;

import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.model.ServiceLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ServiceLocationsExtractorProcessor implements Processor {

  @Override
  public void process(Exchange exchange) {
    HealthDataAssessment assessment = exchange.getMessage().getBody(HealthDataAssessment.class);
    List<ServiceLocation> serviceLocations = assessment.getEvidence().getServiceLocations();
    exchange.setProperty("serviceLocations", serviceLocations);
  }
}
