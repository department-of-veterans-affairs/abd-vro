package gov.va.vro.abddataaccess.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import gov.va.vro.abddataaccess.config.properties.LighthouseProperties;
import gov.va.vro.abddataaccess.service.FhirClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
  private static final FhirContext fhirContext = FhirContext.forR4();

  @Autowired private AppProperties properties;

  @Bean
  public FhirClient vroFhirClient() {
    return new FhirClient();
  }

  @Bean
  public IGenericClient lighthouseClient() {
    LighthouseProperties setup = properties.lighthouseProperties();
    return fhirContext.newRestfulGenericClient(setup.getFhirurl());
  }

  @Bean
  public IParser jsonFhirParser() {
    return fhirContext.newJsonParser();
  }

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
