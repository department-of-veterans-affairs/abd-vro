package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.ToRabbitMqRouteHelper;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.camel.processor.RequestAndMerge;
import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class BgsApiClientRoutes extends RouteBuilder {

  public static final String ADD_BGS_NOTES = "direct:addBgsNotes-start";
  static final String BGSCLIENT_ADDNOTES = "direct:toRabbit-bgsClient-addNotes";
  static final String ADD_NOTES_RETRIES = "direct:addBgsNotesWithRetries";

  private final ProducerTemplate producerTemplate;

  private final BgsApiClient bgsApiClient;

  int RETRY_LIMIT = 5;

  @Override
  public void configure() throws Exception {
    var routeId = "add-bgs-notes-route";
    configureAddNotesRoute(routeId);
    configureRouteToBgsApiClientMicroservice();
    configureRouteToSlackNotification();

    configureMockBgsApiMicroservice();
  }

  private void configureAddNotesRoute(String routeId) {
    from(ADD_BGS_NOTES)
        .routeId(routeId)
        .setExchangePattern(ExchangePattern.InOut)
        // expecting body MasProcessingObject.class
        .log("input: ${exchange.pattern}: h=${headers}: p=${exchange.properties}: ${body.class}")
        .bean(bgsApiClient, "buildRequest")
        .loopDoWhile(simple("${body.pendingRequests.size} > 0"))
        .log("loop: ${body.pendingRequests.size} pendingRequests")
        .to(ADD_NOTES_RETRIES)
        .end(); // of loopDoWhile

    from(ADD_NOTES_RETRIES)
        .doTry()
        .log("microservice request: ${body}")
        .process(requestBgsToAddNotesProcessor())
        .id("requestBgsToAddNotes")
        .doCatch(BgsApiClientException.class)
        .setBody(simple("${body.incrementTryCount()}"))
        .log(
            "caught: ${exception.message}: tryCount=${body.tryCount} will retry in ${body.delayMillis} ms")
        .delay(simple("${body.delayMillis}"))
        .to(ADD_NOTES_RETRIES)
        .end(); // of try-catch block
  }

  private RequestAndMerge<BgsNotesCamelBody, BgsApiClientRequest, BgsApiClientResponse>
      requestBgsToAddNotesProcessor() {
    return RequestAndMerge.<BgsNotesCamelBody, BgsApiClientRequest, BgsApiClientResponse>factory(
            producerTemplate)
        .requestUri(BGSCLIENT_ADDNOTES)
        .prepareRequest(body -> body.currentRequest())
        // set responseClass so that Camel auto-converts JSON into object
        .responseClass(BgsApiClientResponse.class)
        .mergeResponse(
            (body, response) -> {
              body.setResponse(response);
              if (response.getStatusCode() < 300) {
                body.removePendingRequest(body.request);
              } else {
                if (body.tryCount.get() >= RETRY_LIMIT) {
                  body.removePendingRequest(body.request);
                  producerTemplate.requestBody(ENDPOINT_SLACK_BGS_FAILED, body);
                } else {
                  // cause a retry
                  throw new BgsApiClientException(
                      "Failed to add note. collection id: " + body.mpo.getCollectionId());
                }
              }
              return body;
            })
        .build();
  }

  void configureRouteToBgsApiClientMicroservice() {
    new ToRabbitMqRouteHelper(this, BGSCLIENT_ADDNOTES)
        .toMq("bgs-api", "add-note")
        .rabbitMqEndpointId("to-rabbitmq-bgsclient-addnote")
        .responseClass(BgsApiClientResponse.class)
        .createRoute()
        .log("BGS client response: ${body.class}: ${body}");
  }

  private final MasConfig masConfig;
  public static final String ENDPOINT_SLACK_BGS_FAILED = "seda:slack-bgs-api-failure";

  void configureRouteToSlackNotification() {
    var buildErrorMessage =
        FunctionProcessor.<BgsNotesCamelBody, String>fromFunction(
            model ->
                // The mock Slack service expects `collection id: ` to be in the message
                String.format(
                    "Failed to add VBMS notes for claim %s, collection id: %s; status code %s: %s. Note: %s %s",
                    model.request.getVbmsClaimId(),
                    model.mpo.getCollectionId(),
                    model.response.getStatusCode(),
                    model.response.getStatusMessage(),
                    model.request.veteranNote,
                    model.request.claimNotes));

    String webhook = masConfig.getSlackExceptionWebhook();
    String channel = masConfig.getSlackExceptionChannel();

    from(ENDPOINT_SLACK_BGS_FAILED)
        .routeId("slack-bgs-failed-route")
        .filter(exchange -> StringUtils.isNotBlank(webhook))
        .log("slack: ${exchange.pattern}:  ${headers}: ${body.class}: ${body}")
        .process(buildErrorMessage)
        .to(String.format("slack:#%s?webhookUrl=%s", channel, webhook));
  }

  // TODO: remove once BgsApiClientMicroservice is ready for testing
  void configureMockBgsApiMicroservice() {
    final AtomicInteger requestCounter = new AtomicInteger(0);
    var mockService =
        FunctionProcessor.<BgsApiClientRequest, BgsApiClientResponse>builder()
            .inputBodyClass(BgsApiClientRequest.class)
            .function(
                request -> {
                  log.warn("++ MOCK BgsApiClientMicroservice: " + request.toString());
                  var response = new BgsApiClientResponse();
                  if (requestCounter.incrementAndGet() % 7 == 0) {
                    log.warn("++++ Mock success");
                    response.setStatusCode(200);
                  } else {
                    log.warn("++++ Mock error");
                    response.setStatusMessage("Mocked error");
                    response.setStatusCode(400);
                  }
                  return response;
                })
            .build();

    RabbitMqCamelUtils.fromRabbitmq(this, "bgs-api", "add-note").process(mockService);
  }
}
