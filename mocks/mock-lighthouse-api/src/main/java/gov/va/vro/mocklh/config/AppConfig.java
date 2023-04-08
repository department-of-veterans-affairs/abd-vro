package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@Slf4j
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
  @SneakyThrows
  public MockBundleStore muckBundleStore() throws IOException {
    MockBundleStore store = new MockBundleStore();
    String rootName = "mock-bundles";

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    Resource[] resources = resolver.getResources("mock-bundles/mock*");
    for (Resource resource : resources) {
      String icn = resource.getFilename();
      log.info("Loading icn: ", icn);
      MockBundles mb = MockBundles.of("mock-bundles/" + icn);
      store.put(icn, mb);
    }
    return store;
  }
}
