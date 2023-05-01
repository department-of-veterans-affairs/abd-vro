package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.va.vro.model.rrd.claimmetrics.ExamOrderInfoQueryParams;
import gov.va.vro.model.rrd.claimmetrics.response.ExamOrderInfoResponse;
import gov.va.vro.persistence.model.ExamOrderEntity;
import gov.va.vro.persistence.repository.ExamOrderRepository;
import gov.va.vro.service.provider.camel.MasIntegrationRoutes;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ClaimMetricsControllerTest {

    @Autowired private ExamOrderRepository examOrderRepository;
    @EndpointInject("mock: exam-order-slack")
    private MockEndpoint mockExamSlack;

    @Autowired private CamelContext camelContext;

    @Autowired
    TestRestTemplate restTemplate;

    public ObjectMapper createObjectMapper() {
        return JsonMapper.builder().addModule(new JavaTimeModule()).build();
    }

    private final ObjectMapper mapper = createObjectMapper();

    private HttpEntity<Void> getAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", "test-key-01");
        return new HttpEntity<>(headers);
    }
    private ResponseEntity<String> callPostRestWithAuthorization(String uri) {
        HttpEntity<Void> requestEntity = getAuthorizationHeader();

        return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
    }
    @Test
    void testExamOrderSlackResponse() throws Exception {
        examOrderRepository.deleteAll();

        var slackCalled = new AtomicBoolean(false);
        adviceWith(
                camelContext,
                "exam-order-slack",
                route ->
                        route
                                .interceptSendToEndpoint(MasIntegrationRoutes.ENDPOINT_EXAM_ORDER_SLACK)
                                .skipSendToOriginalEndpoint()
                                .to(mockExamSlack))
                .end();
        // The mock endpoint returns a valid response
        mockExamSlack.whenAnyExchangeReceived(
                exchange -> {
                    slackCalled.set(true);
                });

        ExamOrderInfoQueryParams params = new ExamOrderInfoQueryParams(0, 10, Boolean.TRUE);

        String collectionIDFound = "1234";
        LocalDateTime timeFound = LocalDateTime.now().minus(24, ChronoUnit.HOURS);

        Field createdAtField = ExamOrderEntity.class.getSuperclass().getDeclaredField("createdAt");
        createdAtField.setAccessible(true);

        ExamOrderEntity entity1 = new ExamOrderEntity();
        entity1.setOrderedAt(null);
        entity1.setCollectionId(collectionIDFound);
        createdAtField.set(entity1, timeFound);

        ExamOrderEntity entity2 = new ExamOrderEntity();
        entity2.setOrderedAt(null);
        entity2.setCollectionId("1235");
        createdAtField.set(entity2, LocalDateTime.now());

        examOrderRepository.save(entity1);
        examOrderRepository.save(entity2);

        ExamOrderInfoResponse response = new ExamOrderInfoResponse();
        response.setOrderedAt(null);
        response.setCreatedAt(timeFound);
        response.setCollectionId(collectionIDFound);
        // Call exam-order-slack
        ResponseEntity<String> responseEntity =
                callPostRestWithAuthorization("/v2/exam-order-slack?page=0&size=10&notOrdered=true");
        // Expect a 200 and a list of size one
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String body = responseEntity.getBody();
        assertNotNull(body);
        ExamOrderInfoResponse[] actual = mapper.readValue(body, ExamOrderInfoResponse[].class);
        assertEquals(1, actual.length);
        ExamOrderInfoResponse response1 = actual[0];
        assertNotNull(response1);
        assertNull(response1.getOrderedAt());
        assertEquals(response1.getCollectionId(), collectionIDFound);
        assertEquals(response1.getCreatedAt(), timeFound);
        assertTrue(slackCalled.get());
    }
}

