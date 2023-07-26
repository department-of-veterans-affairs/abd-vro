package gov.va.vro.services.bie.config;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.vro.services.bie.service.AmqpMessageSender;
import gov.va.vro.services.bie.service.kafka.KafkaConsumerCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerCreatorTest {

  @Mock private ConsumerFactory<?, ?> consumerFactory;
  @Mock private AmqpMessageSender amqpMessageSender;

  private BieProperties bieProperties;
  private KafkaConsumerCreator kafkaConsumerCreator;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
  }

  @Test
  void whenTopicMapIsEmpty_ShouldCreateNoListeners() {
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of());

    kafkaConsumerCreator =
        new KafkaConsumerCreator(consumerFactory, amqpMessageSender, bieProperties);

    assertThat(kafkaConsumerCreator.getListeners()).isEmpty();
  }

  @Test
  void whenTopicMapIsNotEmpty_ShouldCreateListeners() {
    bieProperties.setKafkaTopicToAmqpExchangeMap(Map.of("kafkaTopic", "rabbitExchange"));

    kafkaConsumerCreator =
        new KafkaConsumerCreator(consumerFactory, amqpMessageSender, bieProperties);

    assertThat(kafkaConsumerCreator.getListeners()).hasSize(1);
    final String[] topics =
        kafkaConsumerCreator.getListeners().get(0).getContainerProperties().getTopics();
    assertThat(topics).isNotNull();
    assertThat(topics).contains("kafkaTopic");
  }
}
