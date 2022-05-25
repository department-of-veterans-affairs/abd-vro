package gov.va.vro.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.WaitForTaskToComplete;
import org.apache.camel.component.seda.SedaEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelUtils {
  @Autowired private CamelContext camelContext;

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
