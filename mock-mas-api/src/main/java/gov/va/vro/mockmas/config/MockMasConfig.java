package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

@Configuration
public class MockMasConfig {

  @Value("classpath:annotations/collection-375.json")
  private Resource collection375Resource;

  @Value("classpath:annotations/collection-376.json")
  private Resource collection376Resource;

  @Value("classpath:annotations/collection-500.json")
  private Resource collection500Resource;

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

  @Bean
  public CollectionStore collectionStore() {
    CollectionStore store = new CollectionStore();

    List<MasCollectionAnnotation> collection375 = readFromResource(collection375Resource);
    store.put(375, collection375);

    List<MasCollectionAnnotation> collection376 = readFromResource(collection376Resource);
    store.put(376, collection376);

    List<MasCollectionAnnotation> collection500 = readFromResource(collection500Resource);
    store.put(500, collection500);

    return store;
  }
}
