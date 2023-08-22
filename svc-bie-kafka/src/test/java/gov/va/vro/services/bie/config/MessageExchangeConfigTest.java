package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MessageExchangeConfigTest {

  private BieProperties bieProperties;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of());
    bieProperties.topicPrefix = "TST_";
    bieProperties.addPrefixToTopicNames();
  }

  @Test
  void createTopicBindingsWithEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(0);
  }

  @Test
  void createTopicBindingsWithNonEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of("kafkaTopic", "rabbitExchange"));
    bieProperties.addPrefixToTopicNames();

    assertArrayEquals(bieProperties.topicNames(), new String[] {"TST_kafkaTopic"});

    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(1);
    assertThat(declarables.getDeclarablesByType(Exchange.class)).hasSize(1);
    assertEquals(
        (declarables.getDeclarablesByType(Exchange.class).get(0)).getName(), "rabbitExchange");
  }
}
