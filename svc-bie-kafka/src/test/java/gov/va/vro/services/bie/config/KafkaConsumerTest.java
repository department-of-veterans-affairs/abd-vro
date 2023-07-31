package gov.va.vro.services.bie.config;

import gov.va.vro.services.bie.service.AmqpMessageSender;
import gov.va.vro.services.bie.service.kafka.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

  @Mock private ConsumerFactory<?, ?> consumerFactory;
  @Mock private AmqpMessageSender amqpMessageSender;

  private BieProperties bieProperties;
  private KafkaConsumer kafkaConsumer;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
  }
}
