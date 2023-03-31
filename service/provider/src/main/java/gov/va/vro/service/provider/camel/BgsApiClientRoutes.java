package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT;

import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.camel.processor.InOnlySyncProcessor;
import gov.va.vro.camel.processor.RequestAndMerge;
import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.model.bgs.BgsApiClientResponse;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BgsApiClientRoutes extends RouteBuilder {

  public static final String ADD_BGS_NOTES = "direct:addBgsNotes";
  private static final String BGSCLIENT_ADDNOTES = "direct:bgsClient-addNotes";

  private final ProducerTemplate producerTemplate;

  @Getter(lazy = true)
  private final InOnlySyncProcessor inOnlyLogJson = createInOnlyLogJsonProcessor();

  private InOnlySyncProcessor createInOnlyLogJsonProcessor() {
    return InOnlySyncProcessor.factory(producerTemplate).uri("direct:logJson").build();
  }

  private final BgsApiClient bgsApiClient;

  @Override
  public void configure() throws Exception {
    var routeId = "add-bgs-notes-route";
    configureAddNotesRoute(routeId);
    configureRouteToBgsApiClientMicroservice();
    configureRouteToSlackNotification();

    from("direct:BgsApiClientError")
        .wireTap(ENDPOINT_NOTIFY_AUDIT) // Send error notification to slack
        // input: MasProcessingObject; output: AuditEvent
        .onPrepare(slackEventProcessor(routeId, "Error adding claim notes in BGS."));

    configureMockBgsApiMicroservice();
  }

  private void configureAddNotesRoute(String routeId) {
    var requestBgsToAddNotes =
        RequestAndMerge.<BgsNotesCamelBody, BgsApiClientRequest, BgsApiClientResponse>factory(
                producerTemplate)
            .requestUri(BGSCLIENT_ADDNOTES)
            .prepareRequest(body -> body.request)
            // set responseClass so that Camel auto-converts JSON into object
            .responseClass(BgsApiClientResponse.class)
            .mergeResponse(
                (body, response) -> {
                  body.setResponse(response);
                  if (response.getStatusCode() >= 300)
                    producerTemplate.requestBody(ENDPOINT_SLACK_BGS_FAILED, body);
                  return body;
                })
            .build();

    from(ADD_BGS_NOTES)
        .routeId(routeId)
        .setExchangePattern(ExchangePattern.InOut)
        // expecting body MasProcessingObject.class
        .log("input: ${exchange.pattern}: h=${headers}: p=${exchange.properties}: ${body.class}")
        // print the body as json
        .process(getInOnlyLogJson())
        .bean(bgsApiClient, "buildRequest")
        .log("total notes: ${body.request.veteranNotes.size} + ${body.request.claimNotes.size}")
        // if there are notes to add, use BGS client
        .choice()
        .when()
        .simple("${body.request.veteranNotes.size} > 0 || ${body.request.claimNotes.size} > 0")
        .process(getInOnlyLogJson())
        .log("microservice request: ${exchange.pattern}: ${body}")
        .process(requestBgsToAddNotes)
        .id("requestBgsToAddNotes")
        .otherwise()
        .log("No notes to add")
        .end()
        .log("output: ${exchange.pattern}: ${body}");

    from("direct:logJson")
        .marshal()
        .json(JsonLibrary.Jackson)
        .log("JSON: ${exchange.pattern}: ${headers}: ${body}");
  }

  void configureRouteToBgsApiClientMicroservice() {
    // TODO: add retry logic
    RabbitMqCamelUtils.addToRabbitmqRoute(this, BGSCLIENT_ADDNOTES, "bgs-api", "add-note")
        .id("to-rabbitmq-bgsclient-addnote")
        .routeId("to-rabbitmq-bgsapi-addnote-route")
        .log("BGS client response: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
    // TODO: if retries failed, notify slack
    ;
  }

  private final MasConfig masConfig;
  public static final String ENDPOINT_SLACK_BGS_FAILED = "seda:slack-bgs-api-failure";

  void configureRouteToSlackNotification() {

    var buildErrorMessage =
        FunctionProcessor.<BgsNotesCamelBody, String>fromFunction(
            model ->
                // The mock Slack service expects `collection id: ` to be in the message
                String.format(
                    "Failed to add VBMS notes for claim %s, collection id: %s; status code %s: %s",
                    model.request.getVbmsClaimId(),
                    model.mpo.getCollectionId(),
                    model.response.getStatusCode(),
                    model.response.getStatusMessage()));

    String webhook = masConfig.getSlackExceptionWebhook();
    String channel = masConfig.getSlackExceptionChannel();
    String slackRoute = String.format("slack:#%s?webhookUrl=%s", channel, webhook);

    from(ENDPOINT_SLACK_BGS_FAILED)
        .routeId("slack-bgs-failed-route")
        .filter(exchange -> StringUtils.isNotBlank(webhook))
        .log("output: ${exchange.pattern}: ${body.class}: ${body}")
        .process(buildErrorMessage)
        .to(slackRoute)
        .process(getInOnlyLogJson());
  }

  // TODO: remove once BgsApiClientMicroservice is ready for testing
  void configureMockBgsApiMicroservice() {
    var mockService =
        FunctionProcessor.<BgsApiClientRequest, BgsApiClientResponse>builder()
            .inputBodyClass(BgsApiClientRequest.class)
            .function(
                request -> {
                  log.warn("++++ MOCK BgsApiClientMicroservice");
                  var response = new BgsApiClientResponse();
                  response.setStatusCode(400);
                  response.setStatusMessage("Mock error to cause Slack notification");
                  return response;
                })
            .build();

    RabbitMqCamelUtils.fromRabbitmq(this, "bgs-api", "add-note").process(mockService);
  }
}
