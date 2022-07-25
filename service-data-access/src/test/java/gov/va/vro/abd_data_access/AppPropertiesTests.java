package gov.va.vro.abd_data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import gov.va.vro.abd_data_access.config.AppProperties;
import gov.va.vro.abd_data_access.config.properties.RabbitMQProperties;

@SpringBootTest
@ActiveProfiles("test")
public class AppPropertiesTests {
    @Autowired
    private AppProperties properties;

    @Test
    public void testRabbitMQProperties() {
       RabbitMQProperties rmqp = properties.rabbitmq();

       assertEquals("claim-submit-exchange", rmqp.getClaimSubmitExchange());
       assertEquals("claim-submit", rmqp.getClaimSubmitQueue());
    }
}
