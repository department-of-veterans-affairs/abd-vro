package gov.va.vro.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.WaitForTaskToComplete;
import org.apache.camel.component.seda.SedaEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class CamelUtils {
  @Autowired private CamelContext camelContext;

  public String endpointsToString(String delimiter) {
    return camelContext.getEndpoints().stream()
        .map(endpoint -> endpointToString(endpoint))
        .collect(Collectors.joining(delimiter));
  }

  public String endpointToString(Endpoint endpoint) {
    String[] configs;

    switch (endpoint.getClass().getSimpleName()) {
      case "SedaEndpoint":
        SedaEndpoint endpoint1 = (SedaEndpoint) endpoint;
        configs =
            new String[] {
              endpoint1.getWaitForTaskToComplete().name(),
              endpoint1.getExchangePattern().name(),
              endpoint.toString()
            };
        break;
      default:
        configs = new String[] {endpoint.toString()};
        break;
    }
    return endpoint.getEndpointBaseUri() + " -- " + String.join(", ", configs);
  }

  public SedaEndpoint asyncSedaEndpoint(String name) {
    String sedaName = sedaEndpointName(name);
    SedaEndpoint endpoint = camelContext.getEndpoint(sedaName, SedaEndpoint.class);
    endpoint.setWaitForTaskToComplete(WaitForTaskToComplete.Never);
    return endpoint;
  }

  public SedaEndpoint multiConsumerSedaEndpoint(String name) {
    String sedaName = sedaEndpointName(name);
    SedaEndpoint endpoint = camelContext.getEndpoint(sedaName, SedaEndpoint.class);
    endpoint.setMultipleConsumers(true);
    return endpoint;
  }

  private String sedaEndpointName(String name) {
    if (name.startsWith("seda:")) return name;
    return "seda:" + name;
  }
}
