package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.mockmas.model.ExamOrderStore;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
public class MockMasConfig {

  @Value("classpath:mock-info-file.json")
  private Resource resource;

  /** Creates and provides the common instance of RestTemplate as a bean for the application. */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @SneakyThrows
  private List<MasCollectionAnnotation> readFromResource(String path) {
    InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
    ObjectMapper mapper = objectMapper();
    return mapper.readValue(stream, new TypeReference<>() {});
  }

  /**
   * Creates a HashMap store and all mock collections and populates the collections in the store.
   *
   * @return CollectionStore
   */
  @Bean
  @SneakyThrows
  public CollectionStore collectionStore() {
    CollectionStore store = new CollectionStore();

    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    String listAsJson = FileCopyUtils.copyToString(reader);
    ObjectMapper mapper = objectMapper();
    JsonNode array = mapper.readTree(resource.getInputStream());
    int start = "collection-".length();
    for (Iterator<JsonNode> it = array.elements(); it.hasNext(); ) {
      String filename = it.next().asText();
      String idAsString = filename.substring(start, start + 3);
      Integer id = Integer.valueOf(idAsString);
      log.info("Creating mock collection {}", idAsString);
      List<MasCollectionAnnotation> collection = readFromResource("annotations/" + filename);
      store.put(id, collection);
    }
    return store;
  }

  @Bean
  public ExamOrderStore examOrderStore() {
    ExamOrderStore store = new ExamOrderStore();
    return store;
  }
}
