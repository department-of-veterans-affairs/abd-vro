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
    log.info("Loading mock bundles from resources");
    MockBundleStore store = new MockBundleStore();

    String baseFolder = "mock-bundles";

    PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
    Resource[] resources = r.getResources(baseFolder + "/*");
    for (Resource mockBundle : resources) {
      log.info("loading a particular mock bundle");
      File mockBundleDir = mockBundle.getFile();
      log.info(
          "File found with name "
              + mockBundleDir.getName()
              + " isDir "
              + mockBundleDir.isDirectory());
      if (mockBundleDir.isDirectory()) {
        String bundlePath = baseFolder + "/" + mockBundleDir.getName();
        log.info("Directory found with path " + bundlePath);
        MockBundles mb = MockBundles.of(bundlePath);
        store.put(mockBundleDir.getName(), mb);
      }
    }

    log.info("returning loaded resources");

    MockBundles mb6 = MockBundles.of("mock-bundles/mock1012666073V986400");
    store.put("mock1012666073V986400", mb6);

    MockBundles mb7 = MockBundles.of("mock-bundles/mock1012666073V986401");
    store.put("mock1012666073V986401", mb7);

    MockBundles mb8 = MockBundles.of("mock-bundles/mock1012666073V986390");
    store.put("mock1012666073V986390", mb8);

    MockBundles mb9 = MockBundles.of("mock-bundles/mock1012666073V986391");
    store.put("mock1012666073V986391", mb9);

    MockBundles mb10 = MockBundles.of("mock-bundles/mock1012666073V986392");
    store.put("mock1012666073V986392", mb10);

    MockBundles mb11 = MockBundles.of("mock-bundles/mock1012666073V986385");
    store.put("mock1012666073V986385", mb11);

    MockBundles mb12 = MockBundles.of("mock-bundles/mock1012666073V986386");
    store.put("mock1012666073V986386", mb12);

    MockBundles mb13 = MockBundles.of("mock-bundles/mock1012666073V986369");
    store.put("mock1012666073V986369", mb13);

    MockBundles mb14 = MockBundles.of("mock-bundles/mock1012666073V986365");
    store.put("mock1012666073V986365", mb14);

    MockBundles mb15 = MockBundles.of("mock-bundles/mock1012666073V986366");
    store.put("mock1012666073V986366", mb15);

    MockBundles mb16 = MockBundles.of("mock-bundles/mock1012666073V986367");
    store.put("mock1012666073V986367", mb16);

    return store;
  }
}
