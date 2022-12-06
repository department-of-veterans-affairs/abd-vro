package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.ServiceProviderConfig;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.support.builder.ValueBuilder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class SaveToFileRoutes extends RouteBuilder {

  final ServiceProviderConfig config;

  @Override
  public void configure() throws Exception {
    // Do not save to file in PROD until we have the encrypted file system in place
    // and the approved updated ATO
    if (config.persistTrackingEnabled) {
      saveIncomingClaimToFile();
      savePdfRequestToFile();
    }
  }

  private String mqPersistFolder() {
    return System.getenv("MQ_PERSIST_FOLDER");
  }

  private void saveIncomingClaimToFile() throws Exception {
    String tapBasename = PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
    RouteDefinition routeDef =
        from(VroCamelUtils.wiretapConsumer("toFile", tapBasename))
            .routeId("saveToFile-" + tapBasename);
    appendHeaders(routeDef).to("file:" + config.baseTrackingFolder + tapBasename);
  }

  private void savePdfRequestToFile() throws Exception {
    String tapBasename = PrimaryRoutes.GENERATE_PDF_WIRETAP;
    RouteDefinition routeDef =
        from(VroCamelUtils.wiretapConsumer("toFile", tapBasename))
            .routeId("saveToFile-" + tapBasename);
    appendHeaders(routeDef).to("file:" + config.baseTrackingFolder + tapBasename);
  }

  private ValueBuilder filepath(String idField) {
    // https://camel.apache.org/components/3.18.x/languages/file-language.html
    return simple("${date:now:yyyy-MM-dd}/").append(jsonpath("." + idField)).append(".json");
  }

  private RouteDefinition appendHeaders(RouteDefinition routeDef) {
    return routeDef.setHeader(Exchange.FILE_NAME, filepath("claimSubmissionId"));
  }
}
