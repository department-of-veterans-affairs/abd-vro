package gov.va.vro.abddataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.abddataaccess.config.AppProperties;
import gov.va.vro.abddataaccess.config.properties.RabbitMqProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AppPropertiesTests {
  @Autowired private AppProperties properties;

  @Test
  public void testRabbitMQProperties() {
    RabbitMqProperties rmqp = properties.rabbitmq();

    assertEquals("claim-submit-exchange", rmqp.getClaimSubmitExchange());
    assertEquals("claim-submit", rmqp.getClaimSubmitQueue());
  }
}
