package gov.va.vro.service.provider.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.support.builder.ValueBuilder;
import org.springframework.stereotype.Component;

@Component
class SaveToFileRoutes extends RouteBuilder {

  static final String SAVE_FILE_ENDPOINT = "file:...";

  @Override
  public void configure() throws Exception {
    saveIncomingClaimToFile();
    savePdfRequestToFile();
  }

  void saveIncomingClaimToFile() throws Exception {
    String tapBasename = PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
    RouteDefinition routeDef = from(VroCamelUtils.wiretapConsumer(tapBasename)).routeId("saveToFile-" + tapBasename);
    appendHeaders(routeDef, "HSET", filepath("claimSubmissionId", "submitted-claim"))
        .to(SAVE_FILE_ENDPOINT);
  }

  void savePdfRequestToFile() throws Exception {
    String tapBasename = PrimaryRoutes.GENERATE_PDF_WIRETAP;
    RouteDefinition routeDef = from(VroCamelUtils.wiretapConsumer(tapBasename)).routeId("saveToFile-" + tapBasename);
    appendHeaders(routeDef, "HSET", filepath("claimSubmissionId", "submitted-pdf"))
        .to(SAVE_FILE_ENDPOINT);
  }

  private ValueBuilder filepath(String idField, String suffix) {
    return constant("tracking-").append(jsonpath("." + idField)).append(suffix);
  }

  private RouteDefinition appendHeaders(
      RouteDefinition routeDef, String command, ValueBuilder filepath) {
    return routeDef
        .setHeader("REPLACEME", body().convertToString());
  }
}
