package gov.va.vro.bip;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.bip.model.BipClaim;
import gov.va.vro.bip.service.BipApiService;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.io.IOException;

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
@ExtendWith(MockitoExtension.class)
@Slf4j
public class RMQIntegrationTest {

    static  String queueName = "getClaimDetailsQueue";
    static String exchangeName = "bipApiExchange";
    static String routingKey = "getClaimDetailsQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;


    @Mock
    BipApiService bipApiService;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.initMocks(this);
        rabbitAdmin.purgeQueue(queueName, true);
    }

    @AfterEach
    private void tearDown() {
        rabbitAdmin.purgeQueue(queueName, true);
    }

    private final SomeDtoModel requestXX = SomeDtoModel.builder().resourceId("320").diagnosticCode("B").build();

    @Test
    void getClaimDetailsQueueOk() {

       routingKey = queueName = "getClaimDetailsQueue";

       BipClaim response = (BipClaim) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, "abc");


       System.out.println(response);
       //assertEquals(response.getClaimId().toString(), 1);
    }
    @Test
    void getClaimDetailsQueueErr() {
        routingKey = queueName = "getClaimDetailsQueue";
        String requestStr = "X";
        Message  response = (Message) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, requestStr);
//        Object  response = (Object) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);
        System.out.println(response.toString());
//        assertEquals(response.getMessageProperties().getHeaders().get("status"), "500");
    }


    @Autowired
    private ObjectMapper objectMapper;

    // Can also send a plain JSON String without any RabbitTemplate auto-conversion of the msg body
    @Test
    void sendJsonMessageGetJsonResponse() throws IOException {
//        var requestJsonStringAsBytes = objectMapper.writeValueAsBytes(request);
//        Message message = new Message(requestJsonStringAsBytes);
//        Message responseMsg = rabbitTemplate.sendAndReceive(exchangeName, routingKey, message);
//        String jsonResponse = new String(responseMsg.getBody());
//        SomeDtoModel response = objectMapper.readValue(jsonResponse, SomeDtoModel.class);
//
//        assertEquals(request.getResourceId(), response.getResourceId());
//        assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
//        assertEquals(StatusValue.DONE.toString(), response.getStatus());
//        assertEquals(200, response.getHeader().getStatusCode());
//        assertNull(response.getHeader().getStatusMessage());
    }

    @Test
    void sendBadDtoMessage() {
//        request.setResourceId("IdThatCausesError");
//        SomeDtoModel response = (SomeDtoModel) rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, request);
//
//        assertEquals(request.getResourceId(), response.getResourceId());
//        assertEquals(request.getDiagnosticCode(), response.getDiagnosticCode());
//        assertNull(response.getStatus());
//        assertEquals(417, response.getHeader().getStatusCode());
//        assertEquals("java.lang.NumberFormatException: For input string: \"IdThatCausesError\"",
//                response.getHeader().getStatusMessage());
    }
}
