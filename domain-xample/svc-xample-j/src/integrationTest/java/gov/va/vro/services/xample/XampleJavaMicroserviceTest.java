package gov.va.vro.services.xample;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static gov.va.vro.services.xample.JavaMicroserviceApplication.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Start RabbitMQ:
 * docker compose up -d rabbitmq-service
 * <p>
 * Run these tests:
 * ./gradlew :domain-xample:svc-xample-j:integrationTest
 */
@SpringBootTest
@Slf4j
public class XampleJavaMicroserviceTest {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RabbitAdmin rabbitAdmin;

  @BeforeEach
  void setUp() {
    rabbitAdmin.purgeQueue(queueName, true);
  }

  @AfterEach
  void tearDown() {
    rabbitAdmin.purgeQueue(queueName, true);
  }

  SomeDtoModel request = SomeDtoModel.builder().resourceId("320").diagnosticCode("B").build();

  @Test
  void sendDtoMessage() {
    SomeDtoModel response = (SomeDtoModel) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertEquals(StatusValue.DONE.toString(), response.getStatus());
    assertEquals(200, response.getHeader().getStatusCode());
    assertNull(response.getHeader().getStatusMessage());
  }

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void sendMessageToGetJson() throws IOException {
    Message message = rabbitTemplate.getMessageConverter().toMessage(request, new MessageProperties());
    Message responseMsg = rabbitTemplate.sendAndReceive(exchangeName, routingKey, message);
    var json = new String(responseMsg.getBody());
    SomeDtoModel response = objectMapper.reader().readValue(json, SomeDtoModel.class);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertEquals(StatusValue.DONE.toString(), response.getStatus());
    assertEquals(200, response.getHeader().getStatusCode());
    assertNull(response.getHeader().getStatusMessage());
  }

  @Test
  void sendBadDtoMessage() {
    request.setResourceId("IdThatCausesError");
    SomeDtoModel response = (SomeDtoModel) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertNull(response.getStatus());
    assertEquals(417, response.getHeader().getStatusCode());
    assertEquals("java.lang.NumberFormatException: For input string: \"IdThatCausesError\"",
        response.getHeader().getStatusMessage());
  }
}
