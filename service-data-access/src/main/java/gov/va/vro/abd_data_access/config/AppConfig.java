package gov.va.vro.abd_data_access.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import gov.va.vro.abd_data_access.config.properties.LighthouseSetup;
import gov.va.vro.abd_data_access.service.FhirClient;

import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    final static private FhirContext fhirContext = FhirContext.forR4();

    @Autowired
    private AppProperties properties;

    @Bean
    public FhirClient vroFhirClient() {
        return new FhirClient();
    }

    @Bean
    public IGenericClient lighthouseClient() {
        LighthouseSetup setup = properties.lighthouseSetup();
        return fhirContext.newRestfulGenericClient(setup.getFhirurl());
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
