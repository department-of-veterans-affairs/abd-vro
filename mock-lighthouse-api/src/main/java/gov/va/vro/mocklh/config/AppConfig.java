package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public MockBundleStore muckBundleStore() throws IOException {
    MockBundleStore store = new MockBundleStore();

    MockBundles mb1 = MockBundles.of("mock-bundles/mock1012666073V986297");
    store.put("1012666073V986297", mb1);

    MockBundles mb2 = MockBundles.of("mock-bundles/mock1012666073V986377");
    store.put("1012666073V986377", mb2);

    return store;
  }
}
