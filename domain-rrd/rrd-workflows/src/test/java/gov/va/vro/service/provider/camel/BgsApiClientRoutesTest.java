package gov.va.vro.service.provider.camel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.model.rrd.bgs.BgsApiClientRequest;
import gov.va.vro.model.rrd.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.MasProcessingObjectTestData;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import gov.va.vro.service.provider.mas.MasProcessingObject;
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

import java.util.ArrayList;
import java.util.stream.IntStream;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class BgsApiClientRoutesTest extends CamelTestSupport {

  public static final String TEST_BGS_CLIENT_SERVICE = "direct:test-microservice";
  @Mock BgsApiClient client;

  @Override
  protected RoutesBuilder createRouteBuilder() {
    mockBgsApiClient();
    MasConfig masConfig = MasConfig.builder().slackExceptionWebhook("http://nowhere").build();
    var rb = new BgsApiClientRoutes(template, client, masConfig);
    configureMockBgsApiMicroservice(rb);
    return rb;
  }

  // See https://camel.apache.org/manual/advice-with.html#_enabling_advice_during_testing
  @Override
  public boolean isUseAdviceWith() {
    return true;
  }

  public static final String CLAIM_ID_MULTIPLE_REQUESTS = "111";
  public static final String CLAIM_ID_RETRY_FAILED = "222";

  void configureMockBgsApiMicroservice(RouteBuilder rb) {
    rb.from(TEST_BGS_CLIENT_SERVICE)
        .process(
            FunctionProcessor.<BgsApiClientRequest, BgsApiClientResponse>builder()
                .inputBodyClass(BgsApiClientRequest.class)
                .function(
                    request -> {
                      var response = new BgsApiClientResponse();
                      switch (request.getVbmsClaimId()) {
                        case CLAIM_ID_MULTIPLE_REQUESTS:
                          response.setStatusCode(200);
                          break;
                        case CLAIM_ID_RETRY_FAILED:
                          if ("pid2".equals(request.getVeteranParticipantId()))
                            response.setStatusCode(400);
                          else response.setStatusCode(200);
                          break;
                      }
                      // return an error on the second request
                      return response;
                    })
                .build());
  }

  static final int NUM_REQUESTS = 2;

  private void mockBgsApiClient() {
    when(client.buildRequests(any()))
        .thenAnswer(
            invocation -> {
              final var mpo = invocation.getArgument(0, MasProcessingObject.class);
              // For testing, set tiny delay between retries
              BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 200);
              IntStream.rangeClosed(1, NUM_REQUESTS)
                  // For testing, use the veteranParticipantId to uniquely identify each request
                  .forEach(
                      i ->
                          body.pendingRequests.add(
                              new BgsApiClientRequest(mpo.getBenefitClaimId(), "pid" + i)));
              return body;
            });
  }

  public static final String MOCK_SLACK = "mock:slack";

  @BeforeEach
  @SneakyThrows
  void mockEndpointsThenStart() {
    AdviceWith.adviceWith(
        context,
        "to-rabbitmq-bgs-api-add-note-route",
        false,
        rb -> {
          // replace the rabbitmq endpoint to avoid "Failed to create connection."
          rb.weaveById("to-rabbitmq-bgsclient-addnote").replace().to(TEST_BGS_CLIENT_SERVICE);
          // mock so we can count the number of requests sent to client service
          rb.mockEndpoints(TEST_BGS_CLIENT_SERVICE);
        });

    AdviceWith.adviceWith(
        context,
        "slack-bgs-failed-route",
        false,
        rb -> {
          // replace the rabbitmq endpoint to avoid "Failed to create connection."
          rb.weaveById("message-to-slack").replace().to(MOCK_SLACK);
        });

    if (isUseAdviceWith()) context.start();
  }

  @Test
  @SneakyThrows
  void multipleRequestsTest() {
    MasProcessingObject mpo =
        MasProcessingObjectTestData.builder().claimId(CLAIM_ID_MULTIPLE_REQUESTS).build().create();
    template.sendBody(BgsApiClientRoutes.ADD_BGS_NOTES, mpo);

    getMockEndpoint("mock:" + TEST_BGS_CLIENT_SERVICE).expectedMessageCount(NUM_REQUESTS);
    assertMockEndpointsSatisfied();
  }

  @Test
  @SneakyThrows
  void retryFailedRequestsTest() {
    MasProcessingObject mpo =
        MasProcessingObjectTestData.builder().claimId(CLAIM_ID_RETRY_FAILED).build().create();
    template.sendBody(BgsApiClientRoutes.ADD_BGS_NOTES, mpo);

    // 1st request succeeds; tries the 2nd request RETRY_LIMIT times
    // https://github.com/department-of-veterans-affairs/abd-vro/issues/1343
    getMockEndpoint("mock:" + TEST_BGS_CLIENT_SERVICE)
        .expectedMessageCount(NUM_REQUESTS - 1 + BgsApiClientRoutes.RETRY_LIMIT);

    // Since the 2nd request fails all retries, a Slack message is sent
    // https://github.com/department-of-veterans-affairs/abd-vro/issues/1342
    var msg =
        String.format(
            "Failed to add VBMS notes for claim %s, collection id: %s; status code %s: %s. Note: %s %s",
            mpo.getBenefitClaimId(), mpo.getCollectionId(), 400, null, null, new ArrayList());
    getMockEndpoint(MOCK_SLACK).expectedBodiesReceived(msg);

    assertMockEndpointsSatisfied();
  }
}
