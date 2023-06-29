package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MessageQueueConfigTest {

  @Test
  void createTopicBindingsWithEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    final BieProperties bieProperties = new BieProperties();
    bieProperties.setTopicMap(Map.of());

    final MessageQueueConfig config = new MessageQueueConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(0);
  }

  @Test
  void createTopicBindingsWithNonEmptyTopicMap_ShouldReturnEmptyListOfDeclarables() {
    final BieProperties bieProperties = new BieProperties();
    bieProperties.setTopicMap(Map.of("kafkaTopic", "rabbitQueue"));

    final MessageQueueConfig config = new MessageQueueConfig();
    final Declarables declarables = config.topicBindings(bieProperties);

    assertThat(declarables).isNotNull();
    assertThat(declarables.getDeclarables()).hasSize(3);
    assertThat(declarables.getDeclarablesByType(Queue.class)).hasSize(1);
    assertThat(declarables.getDeclarablesByType(Exchange.class)).hasSize(1);
    assertThat(declarables.getDeclarablesByType(Binding.class)).hasSize(1);
  }
}
