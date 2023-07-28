package gov.va.vro.services.bie.service;

import gov.va.vro.model.biekafka.BieMessagePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @Captor ArgumentCaptor<BieMessagePayload> messageCaptor;
  }
}
