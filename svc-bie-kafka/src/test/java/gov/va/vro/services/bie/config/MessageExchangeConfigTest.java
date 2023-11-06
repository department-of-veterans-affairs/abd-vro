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
    bieProperties.kakfaTopicPrefix = "TST_";
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
          "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02",
          "TST_CONTENTION_BIE_CONTENTION_UPDATED_V02",
          "TST_CONTENTION_BIE_CONTENTION_CLASSIFIED_V02",
          "TST_CONTENTION_BIE_CONTENTION_COMPLETED_V02",
          "TST_CONTENTION_BIE_CONTENTION_DELETED_V02"
        },
        topicNames);
  }
}
