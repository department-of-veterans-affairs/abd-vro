package gov.va.vro.service.provider.camel;

import gov.va.vro.model.event.AuditEvent;
import gov.va.vro.model.event.Auditable;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/** Extract an AuditEvent from the information on the Exchange. */
public class ExchangeAuditTransformer implements Processor {
  @Override
  public void process(Exchange exchange) {
    String routeId = exchange.getProperty("originalRouteId", String.class);
    if (routeId == null) {
      routeId = exchange.getFromRouteId();
    }
    String endpoint = exchange.getFromEndpoint().getEndpointUri();
    Message message = exchange.getMessage();
    Auditable body = message.getBody(Auditable.class);
    Throwable exception = (Throwable) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
    var event =
        exception == null
            ? AuditEvent.fromAuditable(body, routeId, "Received message from endpoint " + endpoint)
            : AuditEvent.fromException(body, routeId, exception);
    message.setBody(event);
  }
}
