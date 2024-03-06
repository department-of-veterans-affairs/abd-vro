package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Declarables;

@ExtendWith(MockitoExtension.class)
class MessageExchangeConfigTest {

  private BieProperties bieProperties;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
    bieProperties.kakfaTopicPrefix = "EXT_VRO_TST_";
  }

  @Test
  void createTopicBindingsWithContentionEvents_ShouldReturnNumberOfEvents() {
    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(bieProperties.topicNames().length);
  }

  @Test
  void topicNames() {
    final String[] topicNames = bieProperties.topicNames();
    assertArrayEquals(
        new String[] {
          "EXT_VRO_TST_BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM",
          "EXT_VRO_TST_BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED",
          "EXT_VRO_TST_BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED",
          "EXT_VRO_TST_BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED",
          "EXT_VRO_TST_BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED"
        },
        topicNames);
  }
}
