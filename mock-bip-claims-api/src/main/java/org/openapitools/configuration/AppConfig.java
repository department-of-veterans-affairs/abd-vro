package org.openapitools.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class AppConfig {
  @Value("classpath:mock-claims.json")
  private Resource mockClaimsResource;

  @Bean
  public ObjectMapper createObjectMapper() {
    return  JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();
  }

  @Bean
  public ClaimStore createClaimStore() throws IOException {
    ClaimStore claimStore = new ClaimStore();

    File file = mockClaimsResource.getFile();
    byte[] content = Files.readAllBytes(file.toPath());
    ObjectMapper mapper = createObjectMapper();
    ClaimStoreItem[] items = mapper.readValue(content, ClaimStoreItem[].class);
    for (int index=0; index < items.length; ++index) {
      ClaimStoreItem item = items[index];
      claimStore.put(item);
    }
    return claimStore;
  }
}
