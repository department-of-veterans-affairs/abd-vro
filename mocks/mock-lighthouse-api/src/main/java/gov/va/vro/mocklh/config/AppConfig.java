package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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

    var resource = this.getClass().getClassLoader().getResource(rootName);
    List<String> directoryNames =
        Files.walk(Paths.get(resource.toURI()), 1)
            .filter(Files::isDirectory)
            .map(p -> p.getFileName().toString())
            .filter(r -> r.startsWith("mock") && !r.equals(rootName))
            .collect(Collectors.toList());

    for (String icn : directoryNames) {
      log.info("Loading icn: ", icn);
      MockBundles mb = MockBundles.of("mock-bundles/" + icn);
      store.put(icn, mb);
    }

    return store;
  }
}
