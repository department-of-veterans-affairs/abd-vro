package gov.va.vro.service.provider.camel;

import static gov.va.vro.service.provider.camel.MasIntegrationProcessors.slackEventProcessor;
import static gov.va.vro.service.provider.camel.MasIntegrationRoutes.ENDPOINT_NOTIFY_AUDIT;

import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.processor.FunctionProcessor;
import gov.va.vro.model.bgs.BgsApiClientModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BgsApiClientRoutes extends RouteBuilder {

  public static final String ADD_BGS_NOTES = "direct:addBgsNotes";

  @Override
  public void configure() throws Exception {
    var routeId = "add-bgs-notes";
    configureRouteToBgsApiClientMicroservice();

    // configureRouteToSlackNotification();
    from("direct:BgsApiClientError")
      .wireTap(ENDPOINT_NOTIFY_AUDIT) // Send error notification to slack
      // input: MasProcessingObject; output: AuditEvent
      .onPrepare(slackEventProcessor(routeId, "Error adding claim notes in BGS."));

    configureMockBgsApiMicroservice();
  }

  void configureRouteToBgsApiClientMicroservice() {
    RabbitMqCamelUtils.addToRabbitmqRoute(this, ADD_BGS_NOTES, "bgsApi", "addNote")
        .convertBodyTo(BgsApiClientModel.class);
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
        new FunctionProcessor<BgsApiClientModel, BgsApiClientModel>(
            BgsApiClientModel.class,
            model -> {
              model.setStatusCode(400);
              return model;
            });

    RabbitMqCamelUtils.fromRabbitmq(this, "bgsApi", "addNote").process(returnError);
  }
}
