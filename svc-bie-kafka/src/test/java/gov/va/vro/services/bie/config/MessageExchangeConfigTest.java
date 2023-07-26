package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MessageExchangeConfigTest {

  @Test
  void createTopicBindingsWithEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    final BieProperties bieProperties = new BieProperties();
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of());

    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(0);
  }

  @Test
  void createTopicBindingsWithNonEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    final BieProperties bieProperties = new BieProperties();
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of("kafkaTopic", "rabbitExchange"));

    final MessageExchangeConfig config = new MessageExchangeConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(1);
    assertThat(declarables.getDeclarablesByType(Exchange.class)).hasSize(1);
  }
}
