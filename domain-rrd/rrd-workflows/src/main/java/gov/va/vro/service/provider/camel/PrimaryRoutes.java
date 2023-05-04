package gov.va.vro.service.provider.camel;

import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.camel.RabbitMqCamelUtils;
import gov.va.vro.camel.ToRabbitMqRouteHelper;
import gov.va.vro.service.provider.camel.processor.AssessmentResultProcessor;
import gov.va.vro.service.provider.camel.processor.EvidenceSummaryDocumentProcessor;
import gov.va.vro.service.spi.db.SaveToDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** Defines primary routes. */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryRoutes extends RouteBuilder {
  public static final String ENDPOINT_SUBMIT_CLAIM_FULL = "direct:claim-submit-full";
  public static final String ENDPOINT_GENERATE_PDF = "direct:generate-pdf";
  public static final String ENDPOINT_FETCH_PDF = "direct:fetch-pdf";
  public static final String ENDPOINT_GENERATE_FETCH_PDF = "direct:generate-fetch-pdf";

  private static final String PDF_EXCHANGE = "pdf-generator";
  private static final String GENERATE_PDF_QUEUE = "generate-pdf";
  private static final String FETCH_PDF_QUEUE = "fetch-pdf";
  private static final String GENERATE_FETCH_PDF_QUEUE = "generate-fetch-pdf";

  // Base names for wiretap endpoints
  public static final String INCOMING_CLAIM_WIRETAP = "claim-submitted";
  public static final String GENERATE_PDF_WIRETAP = "generate-pdf";

  private final SaveToDbService saveToDbService;

  private final AssessmentResultProcessor assessmentResultProcessor;
  private final EvidenceSummaryDocumentProcessor evidenceSummaryDocumentProcessor;
  private final SlipClaimSubmitRouter slipClaimSubmitRouter;

  @Override
  public void configure() throws Exception {
    configureRouteClaimSubmitForFull();
    configureRouteGeneratePdf();
    configureRouteFetchPdf();
    configureRouteimmediatePdf();
  }

  private void configureRouteClaimSubmitForFull() {
    // send JSON-string payload to RabbitMQ
    from(ENDPOINT_SUBMIT_CLAIM_FULL)
        .routeId("claim-submit-full")
        .wireTap(RabbitMqCamelUtils.wiretapProducer(this, INCOMING_CLAIM_WIRETAP))
        .process(FunctionProcessor.fromFunction(saveToDbService::insertClaim))
        // Use Properties not Headers
        // https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example/
        .setProperty("diagnosticCode", simple("${body.diagnosticCode}"))
        .setProperty("claim-id", simple("${body.recordId}"))
        .routingSlip(method(slipClaimSubmitRouter, "routeClaimSubmit"))
        .routingSlip(method(slipClaimSubmitRouter, "routeHealthAssess"))
        .process(assessmentResultProcessor);
  }

  private void configureRouteGeneratePdf() {
    from(ENDPOINT_GENERATE_PDF)
        .routeId("generate-pdf")
        .wireTap(RabbitMqCamelUtils.wiretapProducer(this, GENERATE_PDF_WIRETAP))
        .process(evidenceSummaryDocumentProcessor)
        .to(pdfRoute(GENERATE_PDF_QUEUE));
  }

  private void configureRouteFetchPdf() {
    from(ENDPOINT_FETCH_PDF).routeId("fetch-pdf").to(pdfRoute(FETCH_PDF_QUEUE));
  }

  private void configureRouteimmediatePdf() {
    from(ENDPOINT_GENERATE_FETCH_PDF)
        .routeId("generate-fetch-pdf")
        .to(pdfRoute(GENERATE_FETCH_PDF_QUEUE));
  }

  private String pdfRoute(String queueName) {
    String uri = "direct:rabbitmq-" + queueName;
    new ToRabbitMqRouteHelper(this, uri).toMq(PDF_EXCHANGE, queueName).createRoute();
    return uri;
  }
}
