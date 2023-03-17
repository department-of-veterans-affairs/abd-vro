package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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

  /**
   * Mock bundle store.
   *
   * @return store
   * @throws IOException Missing files
   */
  @Bean
  public MockBundleStore muckBundleStore() throws IOException {
    MockBundleStore store = new MockBundleStore();

    String baseFolder = "mock-bundles";

    PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
    Resource[] resources = r.getResources("/" + baseFolder + "/*");
    for (Resource mockBundle : resources) {
      File mockBundleDir = mockBundle.getFile();
      if (mockBundleDir.isDirectory()) {
        String bundlePath = baseFolder + "/" + mockBundleDir.getName();
        MockBundles mb = MockBundles.of(bundlePath);
        store.put(mockBundleDir.getName(), mb);
      }
    }

    return store;
  }
}
