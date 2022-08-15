package gov.va.vro;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.HealthDataAssessmentResponse;
import gov.va.vro.service.provider.camel.FunctionProcessor;
import gov.va.vro.service.spi.model.Claim;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Configure mock routes for testing */
@Component
@Profile("test")
public class CamelTestRoutes extends RouteBuilder {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure() {

    from("direct:hello").process(FunctionProcessor.fromFunction(this::claimToResponse));
  }

  @SneakyThrows
  private String claimToResponse(Claim claim) {
    var response = new HealthDataAssessmentResponse();
    response.setDiagnosticCode(claim.getDiagnosticCode());
    response.setVeteranIcn(claim.getVeteranIcn());
    response.setErrorMessage("I am not a real endpoint.");
    return objectMapper.writeValueAsString(response);
  }
}
