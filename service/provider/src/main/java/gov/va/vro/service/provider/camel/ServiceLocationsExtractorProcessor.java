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
    try { // Late change, be defensive
      HealthDataAssessment assessment = exchange.getMessage().getBody(HealthDataAssessment.class);
      List<ServiceLocation> serviceLocations = assessment.getEvidence().getServiceLocations();
      exchange.setProperty("serviceLocations", serviceLocations);
    } catch (Exception e) {
      // If this happens service locations in the pdf will be empty
      log.info("unable to set the service location", e);
    }
  }
}
