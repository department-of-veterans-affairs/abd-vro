package gov.va.vro.routes.xample;

import static gov.va.vro.model.xample.CamelConstants.POST_RESOURCE_QUEUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.model.xample.StatusValue;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class XampleRoutesTest extends CamelTestSupport {

  public static final String STARTING_URI = "seda:" + POST_RESOURCE_QUEUE;

  @Mock DbHelper dbHelper;

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new XampleRoutes(dbHelper);
  }

  // See https://camel.apache.org/manual/advice-with.html#_enabling_advice_during_testing
  @Override
  public boolean isUseAdviceWith() {
    return true;
  }

  public static final String logOutput = "mock:logOutput";
  public static final String serviceJ = "mock:serviceJ";

  @BeforeEach
  @SneakyThrows
  void mockEndpoints() {
    AdviceWith.adviceWith(
        context,
        "fancyService-route",
        false,
        rb -> {
          // replace rabbitmq endpoint with a mock so we don't have to set up rabbitmq
          rb.weaveById("serviceJ-via-rabbitmq").replace().to(serviceJ);
          rb.weaveAddLast().to(logOutput);
        });
    AdviceWith.adviceWith(
        context,
        "postXResource-route",
        false,
        rb -> {
          // replace the rabbitmq with seda
          rb.replaceFromWith(STARTING_URI);
          rb.mockEndpoints("direct:saveToDb");
          rb.weaveAddLast().to(logOutput);
        });
    AdviceWith.adviceWith(
        context,
        "to-rabbitmq-route",
        false,
        rb -> {
          // replace the rabbitmq endpoint to avoid "Failed to create connection."
          // https://tomd.xyz/mock-endpoints-are-real: "Original endpoints are still initialised,
          // even if they have been mocked."
          // The route processor id was assigned in RabbitMqCamelUtils.addToRabbitmqRoute().
          rb.weaveById("to-rabbitmq-xample-serviceJ").replace().to("mock:to-rabbitmq");
        });

    if (isUseAdviceWith()) context.start();
  }

  SomeDtoModel someDtoModel = SomeDtoModel.builder().resourceId("320").diagnosticCode("A").build();

  @Test
  @SneakyThrows
  void testMainRouteWithServiceA() {
    setExpectations(someDtoModel, "whenExchangeReceived");

    // send a message in the original route
    var response = template.requestBody(STARTING_URI, someDtoModel, SomeDtoModel.class);
    assertEquals(someDtoModel.getResourceId(), response.getResourceId());
    assertEquals(StatusValue.PROCESSING.name(), response.getStatus());

    assertMockEndpointsSatisfied();
  }

  @Test
  @SneakyThrows
  void testMainRouteWithServiceB() {
    someDtoModel.setDiagnosticCode("B");
    setExpectations(someDtoModel, "expectedBodiesReceived");

    // send a message in the original route
    var response = template.requestBody(STARTING_URI, someDtoModel, SomeDtoModel.class);
    assertEquals(someDtoModel.getResourceId(), response.getResourceId());
    assertEquals(StatusValue.PROCESSING.name(), response.getStatus());

    assertMockEndpointsSatisfied();
  }

  void setExpectations(SomeDtoModel someDtoModel, String choice) {
    // https://javadoc.io/doc/org.apache.camel/camel-mock/latest/org/apache/camel/component/mock/MockEndpoint.html
    getMockEndpoint(logOutput).expectedMessageCount(2);

    // Demonstrating different ways to check expected message body content:
    if ("whenExchangeReceived".equals(choice)) {
      log.info("Making assertions when message is received");
      // The index parameter starts at 1, not 0
      getMockEndpoint(logOutput)
          .whenExchangeReceived(
              1,
              exchange ->
                  assertEquals(
                      StatusValue.PROCESSING.name(),
                      exchange.getIn().getBody(SomeDtoModel.class).getStatus()));
      getMockEndpoint(logOutput)
          .whenExchangeReceived(
              2,
              exchange ->
                  assertEquals(
                      StatusValue.DONE.name(),
                      exchange.getIn().getBody(SomeDtoModel.class).getStatus()));
    } else if ("expectedBodiesReceived".equals(choice)) {
      log.info("Comparing body with expected objects");
      // Use lombok's toBuilder() to shallow copy someDtoModel, then adjust some fields
      var expectedResponseToCaller =
          someDtoModel.toBuilder().status(StatusValue.PROCESSING.name()).build();
      var expectedOutputFromService =
          someDtoModel.toBuilder().status(StatusValue.DONE.name()).build();
      getMockEndpoint(logOutput)
          .expectedBodiesReceived(expectedResponseToCaller, expectedOutputFromService);
    } else {
      getMockEndpoint(logOutput)
          .whenAnyExchangeReceived(
              exchange -> {
                log.info("logOutput body: {}", exchange.getMessage().getBody());
              });
    }
  }

  @Test
  @SneakyThrows
  void testMainRouteOnException() {
    var errorMsg = "Testing exception handling";
    // This mock endpoint was created above using `rb.mockEndpoints("direct:saveToDb")`
    getMockEndpoint("mock:direct:saveToDb")
        .whenAnyExchangeReceived(
            exchange -> {
              throw new RuntimeException(errorMsg);
            });

    // send a message in the original route
    var response = template.requestBody(STARTING_URI, someDtoModel, SomeDtoModel.class);
    assertEquals(someDtoModel.getResourceId(), response.getResourceId());
    assertEquals(StatusValue.ERROR.name(), response.getStatus());
    assertTrue(response.getStatusMessage().contains(errorMsg));

    assertMockEndpointsSatisfied();
  }
}
