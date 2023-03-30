package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT;

import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.camel.processor.InOnlySyncProcessor;
import gov.va.vro.model.bgs.BgsApiClientDto;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
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

    // configureRouteToSlackNotification();
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
        // detect scenarios
        .bean(bgsApiClient, "buildRequest")
        // if notesToAdd
        .to(BGSCLIENT_ADDNOTES)
        .log("output: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");

    from("direct:logJson")
        .marshal()
        .json(JsonLibrary.Jackson)
        .log("JSON: ${exchange.pattern}: ${headers}: ${body.class}: ${body}");
  }

  void configureRouteToBgsApiClientMicroservice() {
    // TODO: add error handling and retry
    RabbitMqCamelUtils.addToRabbitmqRoute(this, BGSCLIENT_ADDNOTES, "bgs-api", "add-note")
        .id("to-rabbitmq-bgsclient-addnote")
        .routeId("to-rabbitmq-bgsapi-addnote-route")
    // .convertBodyTo(BgsApiClientModel.class)
    // TODO: if retries failed, notify slack
    ;
  }

  // private final MasConfig masConfig;
  // static final String ENDPOINT_NOTIFY_SLACK = "seda:slack-event";
  // void configureRouteToSlackNotification(){
  //   String webhook = masConfig.getSlackExceptionWebhook();
  //   String channel = masConfig.getSlackExceptionChannel();
  //   String slackRoute = String.format("slack:#%s?webhookUrl=%s", channel, webhook);
  //
  //   from(ENDPOINT_NOTIFY_SLACK)
  //       .routeId("mas-slack-event")
  //       .filter(exchange -> StringUtils.isNotBlank(webhook))
  //       .process(FunctionProcessor.fromFunction(AuditEvent::toString))
  //       .to(slackRoute);
  // }

  // TODO: remove once BgsApiClientMicroservice is ready for testing
  void configureMockBgsApiMicroservice() {
    var returnError =
        new FunctionProcessor<BgsApiClientDto, BgsApiClientDto>(
            BgsApiClientDto.class,
            model -> {
              model.setStatusCode(400);
              return model;
            });

    RabbitMqCamelUtils.fromRabbitmq(this, "bgsApi", "addNote").process(returnError);
  }
}
