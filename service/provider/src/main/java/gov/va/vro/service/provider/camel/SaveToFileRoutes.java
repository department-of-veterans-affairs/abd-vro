package gov.va.vro.service.provider.camel;

import gov.va.vro.service.provider.ServiceProviderConfig;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.ValueBuilder;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@RequiredArgsConstructor
@Component
class SaveToFileRoutes extends RouteBuilder {

  final ServiceProviderConfig config;

  @Override
  public void configure() {
    if (config.persistTrackingEnabled) {
      // for v1
      saveRequestToFile(PrimaryRoutes.INCOMING_CLAIM_WIRETAP, "claimSubmissionId");
      saveRequestToFile(PrimaryRoutes.GENERATE_PDF_WIRETAP, "claimSubmissionId");

      // for v2
      saveRequestToFile(MasIntegrationRoutes.MAS_CLAIM_WIRETAP, "collectionId");
      saveRequestToFile(MasIntegrationRoutes.EXAM_ORDER_STATUS_WIRETAP, "collectionId");
    }
  }

  private void saveRequestToFile(String tapBasename, String idField) {
    from(VroCamelUtils.wiretapConsumer("toFile", tapBasename))
        .routeId("saveToFile-" + tapBasename)
        .setHeader(Exchange.FILE_NAME, filepath(idField))
        .to("file:" + Paths.get(config.baseTrackingFolder, tapBasename));
  }

  private ValueBuilder filepath(String idField) {
    // https://camel.apache.org/components/3.18.x/languages/file-language.html
    return simple("${date:now:yyyy-MM-dd}/").append(jsonpath("." + idField)).append(".json");
  }
}
