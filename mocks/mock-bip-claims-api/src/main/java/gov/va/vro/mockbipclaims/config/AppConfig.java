package gov.va.vro.mockbipclaims.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.va.vro.mockbipclaims.model.bip.SpecialIssueType;
import gov.va.vro.mockbipclaims.model.store.ClaimStore;
import gov.va.vro.mockbipclaims.model.store.ClaimStoreItem;
import gov.va.vro.mockbipclaims.model.store.SpecialIssueTypesStore;
import gov.va.vro.mockbipclaims.model.store.UpdatesStore;
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

  @Value("classpath:mock-special-issues.json")
  private Resource mockSpecialIssuesResource;

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
   * Creates a basic HashMap based store.
   *
   * @return The Claimstore.
   * @throws IOException If mock claims data cannot be read
   */
  @Bean
  public SpecialIssueTypesStore createSpecialIssueTypesStore() throws IOException {
    SpecialIssueTypesStore typesStore = new SpecialIssueTypesStore();

    InputStream stream = mockSpecialIssuesResource.getInputStream();
    ObjectMapper mapper = createObjectMapper();
    SpecialIssueType[] items = mapper.readValue(stream, SpecialIssueType[].class);
    for (int index = 0; index < items.length; ++index) {
      SpecialIssueType item = items[index];
      typesStore.put(item);
    }
    return typesStore;
  }

  /**
   * Creates a store for modifying actions.
   *
   * @return the modifying action store
   */
  @Bean
  public UpdatesStore createModifyingActionStore() {
    return new UpdatesStore();
  }
}
