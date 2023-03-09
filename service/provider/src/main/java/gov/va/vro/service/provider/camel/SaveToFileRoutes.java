package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.ServiceProviderConfig;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.ValueBuilder;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RequiredArgsConstructor
@Component
class SaveToFileRoutes extends RouteBuilder {

  final ServiceProviderConfig config;

  @Override
  public void configure() {
    if (config.persistTrackingEnabled) {
      // checks if folder is writeable
      logToFile("VRO started");

      // for v1
      saveRequestToFile(PrimaryRoutes.INCOMING_CLAIM_WIRETAP, "claimSubmissionId");
      saveRequestToFile(PrimaryRoutes.GENERATE_PDF_WIRETAP, "claimSubmissionId");

      // for v2
      saveRequestToFile(MasIntegrationRoutes.MAS_CLAIM_WIRETAP, "collectionId");
      saveRequestToFile(MasIntegrationRoutes.EXAM_ORDER_STATUS_WIRETAP, "collectionId");
    }
  }

  private void logToFile(String fileContents) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    String filename = df.format(Calendar.getInstance().getTime());
    var path = Paths.get(config.baseTrackingFolder, filename);
    try (PrintWriter printWriter = new PrintWriter(new FileWriter(path.toFile()))) {
      printWriter.println(fileContents);
    } catch (IOException e) {
      log.error("Cannot write to " + config.baseTrackingFolder, e);
    }
  }

  private void saveRequestToFile(String tapBasename, String idField) {
    // TODO: make this reusable
    from(VroCamelUtils.wiretapConsumer("toFile", tapBasename))
        // Good practice to clear the CamelRabbitmqExchangeName and CamelRabbitmqRoutingKey so it
        // doesn't interfere with subsequent sending to rabbitmq endpoints
        // https://camel.apache.org/components/3.19.x/rabbitmq-component.html#_troubleshooting_headers:
        // > if the source queue has a routing key set in the headers, it will pass down to
        // > the destination and not be overriden with the URI query parameters.
        .removeHeaders("CamelRabbitmq*")
        .routeId("saveToFile-" + tapBasename)
        .setHeader(Exchange.FILE_NAME, filepath(idField))
        .log("saveRequestToFile: ${headers} ")
        .to("file:" + Paths.get(config.baseTrackingFolder, tapBasename))
        .log("saved ${headers} ${body}");
  }

  private ValueBuilder filepath(String idField) {
    // https://camel.apache.org/components/3.18.x/languages/file-language.html
    return simple("${date:now:yyyy-MM-dd}/").append(jsonpath("." + idField)).append(".json");
  }
}
