package gov.va.vro.consolegroovy

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ConsoleRoutes extends RouteBuilder {

  @Override
  void configure() throws Exception {
    def exchangeName = "health-assess-exchange"
    def diagnosticCode = 7101
    def routingKey = "health-assess.${diagnosticCode}"
    def queueName = "health-assess.${diagnosticCode}"
    String rabbitMqTarget = "rabbitmq:${exchangeName}?queue=assess2console&routingKey=${routingKey}"

//    from(rabbitMqTarget).routeId("assessRoute").to("log:assess")

    // If different queue, all messages are routed to specified queue, overriding existing routing.
    // If same queue, then round-robin.
//    from("rabbitmq:claim-submit-exchange?queue=claim-submit2console&routingKey=code.7101")
//        .routeId("console-claimRoute").to("log:claim")
//
//    from("rabbitmq:pdf-generator?queue=generate-pdf2console&routingKey=generate-pdf")
//        .routeId("console-pdfRoute").to("log:pdf")

    from("rabbitmq:tap-claim-submitted?exchangeType=topic&queue=console-clm-submitted")
        .routeId("console-claim-submitted").to("log:claim-submitted")

    from("rabbitmq:tap-generate-pdf?exchangeType=topic&queue=console-gen-pdf")
        .routeId("console-generate-pdf").to("log:generate-pdf")
  }
}
