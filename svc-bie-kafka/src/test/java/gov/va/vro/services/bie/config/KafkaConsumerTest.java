package gov.va.vro.services.bie.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import gov.va.vro.metricslogging.MetricLoggerService;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import gov.va.vro.services.bie.service.kafka.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class KafkaConsumerTest {

  @Mock private ConsumerFactory<?, ?> consumerFactory;
  @Mock private AmqpMessageSender amqpMessageSender;

  @Autowired private BieProperties bieProperties;

  @Autowired private KafkaConsumer kafkaConsumer;

  @BeforeEach
  void setUp() {
    bieProperties = new BieProperties();
  }

  @Test
  void testMetricsSubmitted() {
    MetricLoggerService metricLoggerService = mock(MetricLoggerService.class);
    kafkaConsumer.setMetricLogger(metricLoggerService);
    kafkaConsumer.consume(new ConsumerRecord<String, Object>("testTopic", 0, 1, "lorem", "ipsum"));

    verify(metricLoggerService)
        .submitCount(MetricLoggerService.METRIC.REQUEST_START, ArgumentMatchers.any());
    verify(metricLoggerService)
        .submitRequestDuration(
            ArgumentMatchers.any(long.class),
            ArgumentMatchers.any(long.class),
            ArgumentMatchers.any());
    verify(metricLoggerService)
        .submitCount(MetricLoggerService.METRIC.RESPONSE_COMPLETE, ArgumentMatchers.any());
  }

  @Test
  void testErrorMetricSubmitted() {
    MetricLoggerService metricLoggerService = mock(MetricLoggerService.class);
    kafkaConsumer.setMetricLogger(metricLoggerService);
    kafkaConsumer.consume(new ConsumerRecord<String, Object>("testTopic", 0, 1, null, null));

    verify(metricLoggerService)
        .submitCount(MetricLoggerService.METRIC.RESPONSE_ERROR, ArgumentMatchers.any());
  }
}
