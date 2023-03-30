package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT;

import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.camel.processor.InOnlySyncProcessor;
import gov.va.vro.model.bgs.BgsApiClientDto;
import gov.va.vro.service.provider.MasConfig;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
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
    from(ADD_BGS_NOTES)
        .routeId(routeId)
        // expecting body MasProcessingObject.class
        .log(
            "input: ${exchange.pattern}: h=${headers}: p=${exchange.properties}: ${body.class}: ${body}")
        // print the body as json
        .process(new InOnlySyncProcessor(producerTemplate, "direct:logJson"))
        // want output of buildRequest to go to BGSCLIENT_ADDNOTES
        .setExchangePattern(ExchangePattern.InOut)
        .bean(bgsApiClient, "buildRequest")
        .log("total notes: ${body.veteranNotes.size} + ${body.claimNotes.size}")
        // if there are notes to add, use BGS client
        .choice()
        .when()
        .simple("${body.veteranNotes.size} > 0 || ${body.claimNotes.size} > 0")
        .process(new InOnlySyncProcessor(producerTemplate, "direct:logJson"))
        .to(BGSCLIENT_ADDNOTES)
        .otherwise()
        .log("No notes to add")
        .end()
        .choice()
        .when()
        .simple("${body.statusCode} >= 300")
        .to(ENDPOINT_SLACK_BGS_FAILED)
        .process(new InOnlySyncProcessor(producerTemplate, "direct:logJson"))
        .end()
        .log("output: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");

    from("direct:logJson")
        .marshal()
        .json(JsonLibrary.Jackson)
        .log("JSON: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
  }

  void configureRouteToBgsApiClientMicroservice() {
    // TODO: add retry logic
    RabbitMqCamelUtils.addToRabbitmqRoute(this, BGSCLIENT_ADDNOTES, "bgs-api", "add-note")
        .id("to-rabbitmq-bgsclient-addnote")
        .routeId("to-rabbitmq-bgsapi-addnote-route")
        .convertBodyTo(BgsApiClientDto.class)
        .log("BGS client response: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
    // TODO: if retries failed, notify slack
    ;
  }

  private final MasConfig masConfig;
  static final String ENDPOINT_SLACK_BGS_FAILED = "seda:slack-bgs-api-failure";

  void configureRouteToSlackNotification() {

    var buildErrorMessage =
        new FunctionProcessor<>(
            BgsApiClientDto.class,
            model ->
                // The mock Slack service expects `collection id: ` to be in the message
                String.format(
                    "Failed to add VBMS notes for claim %s, collection id: %s; status code %s: %s",
                    model.getVbmsClaimId(),
                    model.getCollectionId(),
                    model.getStatusCode(),
                    model.getStatusMessage()));

    String webhook = masConfig.getSlackExceptionWebhook();
    String channel = masConfig.getSlackExceptionChannel();
    String slackRoute = String.format("slack:#%s?webhookUrl=%s", channel, webhook);

    from(ENDPOINT_SLACK_BGS_FAILED)
        .routeId("slack-bgs-failed-route")
        .filter(exchange -> StringUtils.isNotBlank(webhook))
        .process(buildErrorMessage)
        .to(slackRoute);
  }

  // TODO: remove once BgsApiClientMicroservice is ready for testing
  void configureMockBgsApiMicroservice() {
    var returnError =
        new FunctionProcessor<>(
            BgsApiClientDto.class,
            model -> {
              log.warn("++++ USING MOCK BgsApiClientMicroservice");
              model.setStatusCode(400);
              return model;
            });

    RabbitMqCamelUtils.fromRabbitmq(this, "bgs-api", "add-note").process(returnError);
  }
}
