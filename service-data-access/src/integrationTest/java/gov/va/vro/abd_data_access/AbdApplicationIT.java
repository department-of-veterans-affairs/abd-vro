package gov.va.vro.abd_data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.abd_data_access.model.AbdClaim;
import gov.va.vro.abd_data_access.model.AbdResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
//@ContextConfiguration(initializers = AbdApplicationIT.Initializer.class)
@Slf4j
public class AbdApplicationIT {
//  static class Initializer
//      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//    @Override
//    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//      val values =
//          TestPropertyValues.of(
//              "spring.rabbitmq.host=" + rabbitMqContainer.getContainerIpAddress(),
//              "spring.rabbitmq.port=" + rabbitMqContainer.getMappedPort(5672));
//      values.applyTo(configurableApplicationContext);
//    }
//  }
//
//  @TestConfiguration
//  static class Config {
//    @Bean
//    RouteBuilder routeBuilder() {
//      return new RouteBuilder() {
//        @Override
//        public void configure() {
//          String claimSubmitUri =
//              "rabbitmq:claim-submit-exchange"
//                  + "?queue=claim-submit"
//                  + "&routingKey=code.7101"
//                  + "&hostname="
//                  + rabbitMqContainer.getContainerIpAddress()
//                  + "&portNumber="
//                  + rabbitMqContainer.getMappedPort(5672);
//
//          from("direct:access_medical_data")
//              .routeId("medical_data")
//              .to(claimSubmitUri)
//              .unmarshal(new JacksonDataFormat(AbdResponse.class));
//        }
//      };
//    }
//  }
//
//  @Autowired private ProducerTemplate template;
//
//  @Value("classpath:expected-json/lh-patient01-7101.json")
//  private Resource expectedResource;
//
//  private static final DockerImageName rabbitmqImageName =
//      DockerImageName.parse("rabbitmq:3-management-alpine");
//
//  @Container
//  private static RabbitMQContainer rabbitMqContainer =
//      new RabbitMQContainer(rabbitmqImageName)
//          .withLogConsumer(new Slf4jLogConsumer(log))
//          .waitingFor(
//              Wait.forHttp("/api/vhosts").forPort(15672).withBasicCredentials("guest", "guest"));
//
//  @Test
//  void testForClaim01() throws Exception {
//    AbdClaim claim = CommonData.claim01;
//    ObjectMapper mapper = new ObjectMapper();
//    String body = mapper.writeValueAsString(claim);
//
//    AbdResponse response =
//        template.requestBody("direct:access_medical_data", body, AbdResponse.class);
//    assertEquals(claim.getVeteranIcn(), response.getVeteranIcn());
//    assertEquals(claim.getDiagnosticCode(), response.getDiagnosticCode());
//
//    String actual = mapper.writeValueAsString(response.getEvidence());
//
//    InputStream stream = expectedResource.getInputStream();
//    String expected = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
//
//    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
//  }
}
