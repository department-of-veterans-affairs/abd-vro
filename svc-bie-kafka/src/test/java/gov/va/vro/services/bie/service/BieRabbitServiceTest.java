package gov.va.vro.services.bie.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import gov.va.vro.services.bie.service.amqp.BieRabbitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class BieRabbitServiceTest {

  @Mock private RabbitTemplate rabbitTemplate;
  private BieRabbitService bieRabbitService;

  @BeforeEach
  void setUp() {
    this.bieRabbitService = new BieRabbitService(rabbitTemplate);
  }

  @Nested
  class Send {

    @Captor ArgumentCaptor<ContentionEventPayload> messageCaptor;

    @Test
    void shouldConvertAndSendBiePayload() {
      final String exchange = "testExchange";
      final String topic = "testTopic";
      final ContentionEventPayload payload = BieMessagePayloadFactory.create();

      bieRabbitService.send(exchange, topic, payload);
      verify(rabbitTemplate).convertAndSend(eq(exchange), eq(topic), messageCaptor.capture());

      final ContentionEventPayload value = messageCaptor.getValue();
      assertThat(value.getEventType()).isEqualTo(payload.getEventType());
      assertThat(value.getClaimId()).isEqualTo(payload.getClaimId());
      assertThat(value.getContentionId()).isEqualTo(payload.getContentionId());
      assertThat(value.getContentionTypeCode()).isEqualTo(payload.getContentionTypeCode());
      assertThat(value.getContentionClassificationName())
          .isEqualTo(payload.getContentionClassificationName());
      assertThat(value.getActorUserId()).isEqualTo(payload.getActorUserId());
      assertThat(value.getDiagnosticTypeCode()).isEqualTo(payload.getDiagnosticTypeCode());
      assertThat(value.getNotifiedAt()).isEqualTo(payload.getNotifiedAt());
      assertThat(value.getActionName()).isEqualTo(payload.getActionName());
      assertThat(value.getActionResultName()).isEqualTo(payload.getActionResultName());
    }
  }
}
