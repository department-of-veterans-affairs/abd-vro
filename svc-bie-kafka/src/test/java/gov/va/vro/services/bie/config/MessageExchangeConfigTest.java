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

  private BieKafkaProperties bieKafkaProperties;

  @BeforeEach
  void setUp() {
    bieKafkaProperties = new BieKafkaProperties();
    bieKafkaProperties.setKafkaTopicInfix("TST");
  }

  @Test
  void createTopicBindingsWithContentionEvents_ShouldReturnNumberOfEvents() {
    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieKafkaProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(bieKafkaProperties.topicNames().length);
  }

  @Test
  void topicNames() {
    final String[] topicNames = bieKafkaProperties.topicNames();
    assertArrayEquals(
        new String[] {
          "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
          "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_UPDATED_V02",
          "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_CLASSIFIED_V02",
          "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_COMPLETED_V02",
          "BIA_SERVICES_BIE_CATALOG_TST_CONTENTION_DELETED_V02"
        },
        topicNames);
  }
}
