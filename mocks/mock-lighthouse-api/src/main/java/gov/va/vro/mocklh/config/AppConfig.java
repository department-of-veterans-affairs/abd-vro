package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Configuration
public class AppConfig {

  private static final String BASE_FOLDER = "mock-bundles";
  private static final String FOLDER_PATTERN = "classpath:" + BASE_FOLDER + "/*/Condition.json";

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
  public MockBundleStore mockBundleStore() throws IOException {
    MockBundleStore store = new MockBundleStore();
    log.info("Loading mock bundles from classpath resources");

    ClassLoader classLoader = this.getClass().getClassLoader();
    PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver(classLoader);
    Resource[] resources = r.getResources(FOLDER_PATTERN);
    for (Resource mockBundle : resources) {
      String[] pathParts = mockBundle.getURI().toString().split("/");
      String folderName = pathParts[pathParts.length - 2];
      log.info("Found mock bundle folder " + folderName);
      String bundlePath = BASE_FOLDER + "/" + folderName;
      MockBundles mb = MockBundles.of(bundlePath);
      store.put(folderName, mb);
    }

    log.info("Returning loaded resources");

    return store;
  }
}
