package gov.va.vro.mockbipclaims.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.va.vro.mockbipclaims.model.store.ModifyingActionStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class AppConfig {
  @Value("classpath:mock-claims.json")
  private Resource mockClaimsResource;

  @Bean
  public JwtProps jwtProps() {
    return new JwtProps();
  }

  @Bean
  public ObjectMapper createObjectMapper() {
    return JsonMapper.builder().addModule(new JavaTimeModule()).build();
  }

  /**
   * Creates a basic HashMap based store.
   *
   * @return The Claimstore.
   * @throws IOException If mock claims data cannot be read
   */
  @Bean
  public ClaimStore createClaimStore() throws IOException {
    ClaimStore claimStore = new ClaimStore();

    InputStream stream = mockClaimsResource.getInputStream();
    ObjectMapper mapper = createObjectMapper();
    ClaimStoreItem[] items = mapper.readValue(stream, ClaimStoreItem[].class);
    for (int index = 0; index < items.length; ++index) {
      ClaimStoreItem item = items[index];
      item.backupAllCanChange();
      claimStore.put(item);
    }
    return claimStore;
  }

  /**
   * Creates a store for modifying actions.
   *
   * @return the modifying action store
   */
  @Bean
  public ModifyingActionStore createModifyingActionStore() {
    return new ModifyingActionStore();
  }
}
