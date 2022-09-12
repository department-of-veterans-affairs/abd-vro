package gov.va.vro.service.provider.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.WaitForTaskToComplete;
import org.apache.camel.component.seda.SedaEndpoint;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CamelUtils {
  private final CamelContext camelContext;

  /***
   * <p>Endpoints To String.</p>
   *
   * @param delimiter delimiter
   *
   * @return return value
   */
  public String endpointsToString(String delimiter) {
    return camelContext.getEndpoints().stream()
        .map(endpoint -> endpointToString(endpoint))
        .collect(Collectors.joining(delimiter));
  }

  /***
   * <p>Endpoint To String.</p>
   *
   * @param endpoint endpoint
   *
   * @return return
   */
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

  /***
   * <p>Async Seda Endpoint.</p>
   *
   * @param name name
   *
   * @return return
   */
  public SedaEndpoint asyncSedaEndpoint(String name) {
    String sedaName = sedaEndpointName(name);
    SedaEndpoint endpoint = camelContext.getEndpoint(sedaName, SedaEndpoint.class);
    endpoint.setWaitForTaskToComplete(WaitForTaskToComplete.Never);
    return endpoint;
  }

  /***
   * <p>Multi-Consumer Seda Endpoint.</p>
   *
   * @param name name
   *
   * @return return
   */
  public SedaEndpoint multiConsumerSedaEndpoint(String name) {
    String sedaName = sedaEndpointName(name);
    SedaEndpoint endpoint = camelContext.getEndpoint(sedaName, SedaEndpoint.class);
    endpoint.setMultipleConsumers(true);
    return endpoint;
  }

  /***
   * <p>Seda endpoint name.</p>
   *
   * @param name name
   *
   * @return return
   */
  private String sedaEndpointName(String name) {
    if (name.startsWith("seda:")) {
      return name;
    }
    return "seda:" + name;
  }
}
