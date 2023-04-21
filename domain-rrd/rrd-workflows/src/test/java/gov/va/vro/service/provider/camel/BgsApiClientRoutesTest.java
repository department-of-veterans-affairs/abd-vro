package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.BgsApiClientRoutes.ADD_BGS_NOTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.model.rrd.bgs.BgsApiClientRequest;
import gov.va.vro.model.rrd.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasProcessingObjectTestData;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import gov.va.vro.service.provider.mas.MasCamelStage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.IntStream;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class BgsApiClientRoutesTest extends CamelTestSupport {

  public static final String MOCK_BGS_CLIENT_SERVICE = "direct:mock-service";
  @Mock BgsApiClient client;

  @Override
  protected RoutesBuilder createRouteBuilder() {
    mockBgsApiClient();
    MasConfig masConfig = MasConfig.builder().slackExceptionWebhook("someWebhook").build();
    var rb = new BgsApiClientRoutes(template, client, masConfig);
    configureMockBgsApiMicroservice(rb);
    return rb;
  }

  // See https://camel.apache.org/manual/advice-with.html#_enabling_advice_during_testing
  @Override
  public boolean isUseAdviceWith() {
    return true;
  }

  void configureMockBgsApiMicroservice(RouteBuilder rb) {
    rb.from(MOCK_BGS_CLIENT_SERVICE)
        .routeId("mock-bgs-client-service-route")
        .process(
            FunctionProcessor.<BgsApiClientRequest, BgsApiClientResponse>builder()
                .inputBodyClass(BgsApiClientRequest.class)
                .function(
                    request -> {
                      var response = new BgsApiClientResponse();
                      response.setStatusCode(200);
                      return response;
                    })
                .build());
  }

  static final String claimId = "20230421";
  static final int NUM_REQUESTS = 2;

  private void mockBgsApiClient() {
    BgsNotesCamelBody body = new BgsNotesCamelBody(null, 500);
    IntStream.rangeClosed(1, NUM_REQUESTS)
        .forEach(i -> body.pendingRequests.add(new BgsApiClientRequest(claimId, null)));

    when(client.buildRequests(any())).thenReturn(body);
  }

  public static final String logOutput = "mock:logOutput";
  public static final String serviceJ = "mock:serviceJ";

  @BeforeEach
  @SneakyThrows
  void mockEndpointsThenStart() {
    AdviceWith.adviceWith(
        context,
        "to-rabbitmq-bgs-api-add-note-route",
        false,
        rb -> {
          // replace the rabbitmq endpoint to avoid "Failed to create connection."
          rb.weaveById("to-rabbitmq-bgsclient-addnote").replace().to(MOCK_BGS_CLIENT_SERVICE);
          rb.mockEndpoints(MOCK_BGS_CLIENT_SERVICE);
        });

    if (isUseAdviceWith()) context.start();
  }

  @Test
  @SneakyThrows
  void multipleRequestsTest() {
    var stage = MasCamelStage.DURING_PROCESSING;
    var mpo = MasProcessingObjectTestData.builder().masCamelStage(stage).build().create();
    template.sendBody(ADD_BGS_NOTES, mpo);

    getMockEndpoint("mock:" + MOCK_BGS_CLIENT_SERVICE).expectedMessageCount(NUM_REQUESTS);
    assertMockEndpointsSatisfied();
  }
}
