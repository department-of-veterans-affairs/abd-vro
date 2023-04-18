package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.camel.processor.InOnlySyncProcessor;
import gov.va.vro.model.rrd.mas.ClaimDetail;
import gov.va.vro.model.rrd.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class BgsApiClientRoutesTest extends CamelTestSupport {

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("direct:A" + "?exchangePattern=InOnly")
            // .setExchangePattern(ExchangePattern.InOnly)
            .log("input: ${exchange.pattern}: ${headers}: ${body.class}: ${body}")
            .to(ExchangePattern.InOnly, "direct:B2")
            .log("afterB: ${exchange.pattern}: ${headers}: ${body.class}: ${body}")
            .to(ExchangePattern.InOnly, "direct:Z")
            .log("output: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
        ;

        //          .recipientList("direct:B2,direct:Z")

        from("direct:B2")
            .log("B2 input: ${exchange.pattern}: ${headers}: ${body.class}: ${body}")
            .process(
                new FunctionProcessor<MasProcessingObject, String>(
                    MasProcessingObject.class,
                    model -> {
                      try {
                        System.out.println("Waiting...");
                        Thread.sleep(3000);
                      } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                      }
                      return model.toString();
                    }))
            .process(InOnlySyncProcessor.factory(template).uri("direct:B").build())
            // .marshal().json(JsonLibrary.Jackson)
            // .to(ExchangePattern.InOnly,"direct:B")
            .log("B2 JSON: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");

        from("direct:B")
            .marshal()
            .json(JsonLibrary.Jackson)
            .log("JSON: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
        ;

        from("direct:Z").log("inputZ: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
      }
    };
  }

  @Test
  @SneakyThrows
  void testExPattern() {
    context.start();
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setBenefitClaimId("22");
    var payload =
        MasAutomatedClaimPayload.builder().collectionId(10).claimDetail(claimDetail).build();
    var obj = new MasProcessingObject(payload, MasCamelStage.DURING_PROCESSING);
    var output = template.requestBody("direct:A", obj, MasProcessingObject.class);
    System.out.println(output);
  }

  // protected RoutesBuilder createRouteBuilder() {
  //   return new BgsApiClientRoutes();
  // }

  // See https://camel.apache.org/manual/advice-with.html#_enabling_advice_during_testing
  @Override
  public boolean isUseAdviceWith() {
    return true;
  }

  public static final String logOutput = "mock:logOutput";
  public static final String serviceJ = "mock:serviceJ";

  // @BeforeEach
  @SneakyThrows
  void mockEndpoints() {
    AdviceWith.adviceWith(
        context,
        "add-bgs-notes-route",
        false,
        rb -> {
          // replace rabbitmq endpoint with a mock so we don't have to set up rabbitmq
          // rb.weaveById("rabbitmq-bgsapi-addnote").replace().to(serviceJ);
          rb.weaveAddLast().to(logOutput);
        });
    // AdviceWith.adviceWith(
    //     context,
    //     "postXResource-route",
    //     false,
    //     rb -> {
    //       // replace the rabbitmq with seda
    //       // rb.replaceFromWith(STARTING_URI);
    //       // rb.mockEndpoints("direct:saveToDb");
    //       rb.weaveAddLast().to(logOutput);
    //     });
    AdviceWith.adviceWith(
        context,
        "to-rabbitmq-bgsapi-addnote-route",
        false,
        rb -> {
          // replace the rabbitmq endpoint to avoid "Failed to create connection."
          rb.weaveById("to-rabbitmq-bgsclient-addnote").replace().to("mock:to-rabbitmq");
        });

    if (isUseAdviceWith()) context.start();
  }

  // SomeDtoModel someDtoModel =
  // SomeDtoModel.builder().resourceId("320").diagnosticCode("A").build();

  @Test
  @SneakyThrows
  void testMainRouteWithServiceA() {
    // setExpectations(someDtoModel, "whenExchangeReceived");

    // send a message in the original route
    // var response = template.requestBody(ADD_BGS_NOTES, someDtoModel, SomeDtoModel.class);
    // assertEquals(response.getResourceId(), someDtoModel.getResourceId());
    // assertEquals(response.getStatus(), StatusValue.PROCESSING.name());

    assertMockEndpointsSatisfied();
  }
  /*
  @Test
  @SneakyThrows
  void testMainRouteWithServiceB() {
    someDtoModel.setDiagnosticCode("B");
    setExpectations(someDtoModel, "expectedBodiesReceived");

    // send a message in the original route
    var response = template.requestBody(STARTING_URI, someDtoModel, SomeDtoModel.class);
    assertEquals(response.getResourceId(), someDtoModel.getResourceId());
    assertEquals(response.getStatus(), StatusValue.PROCESSING.name());

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
    assertEquals(response.getResourceId(), someDtoModel.getResourceId());
    assertEquals(response.getStatus(), StatusValue.ERROR.name());
    assertTrue(response.getStatusMessage().contains(errorMsg));

    assertMockEndpointsSatisfied();
  }*/
}
