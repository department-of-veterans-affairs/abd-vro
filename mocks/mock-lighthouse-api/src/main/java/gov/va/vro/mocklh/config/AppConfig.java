package gov.va.vro.mocklh.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mocklh.model.MockBundleStore;
import gov.va.vro.mocklh.model.MockBundles;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Configuration
@Slf4j
public class AppConfig {

  @Value("classpath:mock-info-file.json")
  private Resource resource;

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

    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    String listAsJson = FileCopyUtils.copyToString(reader);
    ObjectMapper mapper = objectMapper();
    JsonNode array = mapper.readTree(resource.getInputStream());
    for (Iterator<JsonNode> it = array.elements(); it.hasNext(); ) {
      String icn = it.next().asText();
      log.info("Loading icn: {}", icn);
      MockBundles mb = MockBundles.of("mock-bundles/" + icn);
      store.put(icn, mb);
    }
    return store;
  }
}
