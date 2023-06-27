package gov.va.vro.services.bie.config;

import gov.va.vro.services.bie.service.AmqpTopicSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerCreatorTest {

    @Mock
    private ConsumerFactory<?, ?> consumerFactory;
    @Mock
    private AmqpTopicSender amqpTopicSender;

    private BieProperties bieProperties;
    private KafkaConsumerCreator kafkaConsumerCreator;

    @BeforeEach
    public void setUp() {
        bieProperties = new BieProperties();
    }

    @Test
    public void whenTopicMapIsEmpty_ShouldCreateNoListeners() {
        // Given
        bieProperties.setTopicMap(Map.of());

        // When
        kafkaConsumerCreator = new KafkaConsumerCreator(consumerFactory, amqpTopicSender, bieProperties);

        // Then
        Assertions.assertThat(kafkaConsumerCreator.getListeners()).isEmpty();
    }

    @Test
    public void whenTopicMapIsNoneEmpty_ShouldCreateListeners() {
        // Given
        bieProperties.setTopicMap(Map.of("kafkaTopic", "rabbitQueue"));

        // When
        kafkaConsumerCreator = new KafkaConsumerCreator(consumerFactory, amqpTopicSender, bieProperties);

        // Then
        Assertions.assertThat(kafkaConsumerCreator.getListeners()).hasSize(1);
    }

}