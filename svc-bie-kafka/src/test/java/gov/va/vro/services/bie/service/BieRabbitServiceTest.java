package gov.va.vro.services.bie.service;

import gov.va.vro.services.bie.model.BieMessagePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class BieRabbitServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    private BieRabbitService bieRabbitService;

    @BeforeEach
    public void setUp() {
        this.bieRabbitService = new BieRabbitService(rabbitTemplate);
    }

    @Nested
    class Send {

        @Captor
        ArgumentCaptor<BieMessagePayload> messageCaptor;

        @Test
        public void shouldConvertAndSendBiePayload() {
            // Given
            final String exchange = "testExchange";
            final String topic = "testTopic";
            final String message = "testMessage";

            // When
            bieRabbitService.send(exchange, topic, message);

            // Then
            Mockito.verify(rabbitTemplate).convertAndSend(eq(exchange), eq(topic), messageCaptor.capture());
            final BieMessagePayload payload = messageCaptor.getValue();

            assertThat(payload.getTopic()).isEqualTo(topic);
            assertThat(payload.getNotifiedAt()).isNotBlank();
            assertThat(payload.getEvent()).isEqualTo(message);
        }
    }
}