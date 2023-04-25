package gov.va.vro.service.provider.camel;

import gov.va.vro.model.rrd.AbdEvidence;
import gov.va.vro.model.rrd.HealthDataAssessment;
import gov.va.vro.model.rrd.ServiceLocation;
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
      AbdEvidence evidence = assessment.getEvidence();
      if (evidence != null) {
        List<ServiceLocation> serviceLocations = evidence.getServiceLocations();
        exchange.setProperty("serviceLocations", serviceLocations);

        List<String> docsWout = evidence.getDocumentsWithoutAnnotationsChecked();
        exchange.setProperty("docsWoutAnnotsChecked", docsWout);
      }
    } catch (Exception e) {
      // If this happens service locations in the pdf will be empty
      log.info("unable to set the service location", e);
    }
  }
}
