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

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
public class AppConfig {

  private static final String BASE_FOLDER = "mock-bundles";
  private static final String FOLDER_PATTERN = "classpath:" + BASE_FOLDER + "/*";

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
    log.info("Loading mock bundles from resources");
    MockBundleStore store = new MockBundleStore();

    ClassLoader classLoader = this.getClass().getClassLoader();
    PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver(classLoader);
    Resource[] resources = r.getResources(FOLDER_PATTERN);
    for (Resource mockBundle : resources) {
      log.info("loading a particular mock bundle");
      File mockBundleDir = mockBundle.getFile();
      log.info(
          "File found with name "
              + mockBundleDir.getName()
              + " isDir "
              + mockBundleDir.isDirectory());
      if (mockBundleDir.isDirectory()) {
        String bundlePath = BASE_FOLDER + "/" + mockBundleDir.getName();
        log.info("Directory found with path " + bundlePath);
        MockBundles mb = MockBundles.of(bundlePath);
        store.put(mockBundleDir.getName(), mb);
      }
    }

    log.info("returning loaded resources");

    return store;
  }
}
