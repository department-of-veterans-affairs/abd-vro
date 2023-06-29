package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.vro.services.bie.service.AmqpTopicSender;
import gov.va.vro.services.bie.service.kafka.KafkaConsumerCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Map;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerCreatorTest {

  @Mock private ConsumerFactory<?, ?> consumerFactory;
  @Mock private AmqpTopicSender amqpTopicSender;

  private BieProperties bieProperties;
  private KafkaConsumerCreator kafkaConsumerCreator;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
  }

  @Test
  void whenTopicMapIsEmpty_ShouldCreateNoListeners() {
    // Given
    bieProperties.setTopicMap(Map.of());

    // When
    kafkaConsumerCreator =
        new KafkaConsumerCreator(consumerFactory, amqpTopicSender, bieProperties);

    // Then
    assertThat(kafkaConsumerCreator.getListeners()).isEmpty();
  }

  @Test
  void whenTopicMapIsNotEmpty_ShouldCreateListeners() {
    // Given
    bieProperties.setTopicMap(Map.of("kafkaTopic", "rabbitQueue"));

    // When
    kafkaConsumerCreator =
        new KafkaConsumerCreator(consumerFactory, amqpTopicSender, bieProperties);

    // Then
    assertThat(kafkaConsumerCreator.getListeners()).hasSize(1);
    Pattern topicPattern =
        kafkaConsumerCreator.getListeners().get(0).getContainerProperties().getTopicPattern();
    assertThat(topicPattern).isNotNull();
    assertThat(topicPattern.pattern()).contains(".*kafkaTopic.*");
  }
}
