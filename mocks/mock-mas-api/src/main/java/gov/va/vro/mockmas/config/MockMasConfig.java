package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.mockmas.model.ExamOrderStore;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
public class MockMasConfig {

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
  private List<MasCollectionAnnotation> readFromResource(Resource resource) {
    InputStream stream = resource.getInputStream();
    ObjectMapper mapper = objectMapper();
    return mapper.readValue(stream, new TypeReference<>() {});
  }

  /**
   * Creates a HashMap store and all mock collections and populates the collections in the store.
   *
   * @return CollectionStore
   */
  @Bean
  public CollectionStore collectionStore() throws IOException {
    CollectionStore store = new CollectionStore();

    String baseFolder = "annotations";
    log.info("Loading mock annotations from resources");

    PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
    Resource[] resources = r.getResources(baseFolder + "/*.json");

    for (Resource mockCollection : resources) {
      log.info("Found mock collection with filename " + mockCollection.getFile());
      String collectionId = Objects.requireNonNull(mockCollection.getFilename()).split("[-.]")[1];
      Integer collectionInt = Integer.parseInt(collectionId);
      log.info("Has collection integer" + collectionInt);
      List<MasCollectionAnnotation> collectionList = readFromResource(mockCollection);
      store.put(collectionInt, collectionList);
    }

    log.info("Returning store with mock annotations");
    return store;
  }

  @Bean
  public ExamOrderStore examOrderStore() {
    ExamOrderStore store = new ExamOrderStore();
    return store;
  }
}
