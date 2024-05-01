package gov.va.vro.services.xample;

import static gov.va.vro.services.xample.JavaMicroserviceApplication.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * Start RabbitMQ: docker compose up -d rabbitmq-service
 *
 * <p>Run these tests: ./gradlew :domain-xample:svc-xample-j:integrationTest
 */
@SpringBootTest
@Slf4j
public class XampleJavaMicroserviceTest {

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private RabbitAdmin rabbitAdmin;

  private final SomeDtoModel request =
      SomeDtoModel.builder().resourceId("320").diagnosticCode("B").build();

  @Test
  void sendDtoMessage() {
    SomeDtoModel response =
        (SomeDtoModel) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertEquals(StatusValue.DONE.toString(), response.getStatus());
    assertEquals(200, response.getHeader().getStatusCode());
    assertNull(response.getHeader().getStatusMessage());
  }

  @Autowired private ObjectMapper objectMapper;

  // Can also send a plain JSON String without any RabbitTemplate auto-conversion of the msg body
  @Test
  void sendJsonMessageGetJsonResponse() throws IOException {
    var requestJsonStringAsBytes = objectMapper.writeValueAsBytes(request);
    Message message = new Message(requestJsonStringAsBytes);
    Message responseMsg = rabbitTemplate.sendAndReceive(exchangeName, routingKey, message);
    String jsonResponse = new String(responseMsg.getBody());
    SomeDtoModel response = objectMapper.readValue(jsonResponse, SomeDtoModel.class);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertEquals(StatusValue.DONE.toString(), response.getStatus());
    assertEquals(200, response.getHeader().getStatusCode());
    assertNull(response.getHeader().getStatusMessage());
  }

  @Test
  @Disabled
  void purgeQueue() throws IOException {
    rabbitAdmin.purgeQueue(queueName);
    assertNotNull(rabbitAdmin.getQueueInfo(queueName));
    assertEquals(0, rabbitAdmin.getQueueInfo(queueName).getMessageCount());
  }

  @Test
  void sendBadDtoMessage() {
    request.setResourceId("IdThatCausesError");
    SomeDtoModel response =
        (SomeDtoModel) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);

    assertEquals(request.getResourceId(), response.getResourceId());
    assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
    assertNull(response.getStatus());
    assertEquals(417, response.getHeader().getStatusCode());
    assertEquals(
        "java.lang.NumberFormatException: For input string: \"IdThatCausesError\"",
        response.getHeader().getStatusMessage());
  }
}
