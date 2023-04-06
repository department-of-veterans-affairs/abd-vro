package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.mockmas.model.CollectionStore;
import gov.va.vro.mockmas.model.ExamOrderStore;
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

  @Value("classpath:annotations/collection-377.json")
  private Resource collection377Resource;

  @Value("classpath:annotations/collection-378.json")
  private Resource collection378Resource;

  @Value("classpath:annotations/collection-380.json")
  private Resource collection380Resource;

  @Value("classpath:annotations/collection-381.json")
  private Resource collection381Resource;

  @Value("classpath:annotations/collection-400.json")
  private Resource collection400Resource;

  @Value("classpath:annotations/collection-401.json")
  private Resource collection401Resource;

  @Value("classpath:annotations/collection-500.json")
  private Resource collection500Resource;

  @Value("classpath:annotations/collection-390.json")
  private Resource collection390Resource;

  @Value("classpath:annotations/collection-391.json")
  private Resource collection391Resource;

  @Value("classpath:annotations/collection-392.json")
  private Resource collection392Resource;

  @Value("classpath:annotations/collection-385.json")
  private Resource collection385Resource;

  @Value("classpath:annotations/collection-386.json")
  private Resource collection386Resource;

  @Value("classpath:annotations/collection-365.json")
  private Resource collection365Resource;

  @Value("classpath:annotations/collection-366.json")
  private Resource collection366Resource;

  @Value("classpath:annotations/collection-367.json")
  private Resource collection367Resource;

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
  public CollectionStore collectionStore() {
    CollectionStore store = new CollectionStore();

    List<MasCollectionAnnotation> collection375 = readFromResource(collection375Resource);
    store.put(375, collection375);

    List<MasCollectionAnnotation> collection376 = readFromResource(collection376Resource);
    store.put(376, collection376);

    List<MasCollectionAnnotation> collection377 = readFromResource(collection377Resource);
    store.put(377, collection377);

    List<MasCollectionAnnotation> collection378 = readFromResource(collection378Resource);
    store.put(378, collection378);

    List<MasCollectionAnnotation> collection380 = readFromResource(collection380Resource);
    store.put(380, collection380);

    List<MasCollectionAnnotation> collection381 = readFromResource(collection381Resource);
    store.put(381, collection381);

    List<MasCollectionAnnotation> collection400 = readFromResource(collection400Resource);
    store.put(400, collection400);

    List<MasCollectionAnnotation> collection401 = readFromResource(collection401Resource);
    store.put(401, collection401);

    List<MasCollectionAnnotation> collection500 = readFromResource(collection500Resource);
    store.put(500, collection500);

    List<MasCollectionAnnotation> collection390 = readFromResource(collection390Resource);
    store.put(390, collection390);

    List<MasCollectionAnnotation> collection391 = readFromResource(collection391Resource);
    store.put(391, collection391);

    List<MasCollectionAnnotation> collection392 = readFromResource(collection392Resource);
    store.put(392, collection392);

    List<MasCollectionAnnotation> collection385 = readFromResource(collection385Resource);
    store.put(385, collection385);

    List<MasCollectionAnnotation> collection386 = readFromResource(collection386Resource);
    store.put(386, collection386);

    List<MasCollectionAnnotation> collection365 = readFromResource(collection365Resource);
    store.put(365, collection365);

    List<MasCollectionAnnotation> collection366 = readFromResource(collection366Resource);
    store.put(366, collection366);

    List<MasCollectionAnnotation> collection367 = readFromResource(collection367Resource);
    store.put(367, collection367);

    return store;
  }

  @Bean
  public ExamOrderStore examOrderStore() {
    ExamOrderStore store = new ExamOrderStore();
    return store;
  }
}
