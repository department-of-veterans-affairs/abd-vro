package gov.va.vro.mockmas.config;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
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

    String rootName = "annotations";
    var resource = this.getClass().getClassLoader().getResource(rootName);
    List<String> filenames =
        Files.walk(Paths.get(resource.toURI()), 1)
            .filter(Files::isRegularFile)
            .map(p -> p.getFileName().toString())
            .filter(r -> r.startsWith("collection") && r.endsWith(".json"))
            .map(r -> rootName + "/" + r)
            .collect(Collectors.toList());

    for (String filename: filenames) {
      log.info("Loading file: ", filename);
      String filepath = rootName + "/" + filename;
      String idAsString = filename.substring(10, 13);
      Integer id = Integer.valueOf(idAsString);
      List<MasCollectionAnnotation> collection = readFromResource(filepath);
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
