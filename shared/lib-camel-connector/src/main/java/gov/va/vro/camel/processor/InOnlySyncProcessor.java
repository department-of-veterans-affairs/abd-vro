package gov.va.vro.camel.processor;

import lombok.experimental.SuperBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

/**
 * This InOnly synchronous processor is needed to workaround calls to `.to(ExchangePattern.InOnly,
 * "direct:route2")` (or the equivalent), where route2 has a processor that operates with
 * ExchangePattern.InOut (usually because some Camel component along the route do not support
 * InOnly). When route2's processor runs, the outMessage in the exchange is updated and the original
 * `.to(ExchangePattern.InOnly, "direct:route2")` call is ineffective, i.e., the `.to` endpoint
 * returns outMessage rather than the desired inMessage. A single exchange is used for a route.
 *
 * <p>This InOnlySyncProcessor works by not setting the outMessage of the original exchange and
 * creates a new exchange for the specified uri.
 */
@SuperBuilder(toBuilder = true)
public class InOnlySyncProcessor implements Processor {
  ProducerTemplate producer;

  public static InOnlySyncProcessorBuilder<?, ?> factory(ProducerTemplate producer) {
    return InOnlySyncProcessor.builder().producer(producer);
  }

  String uri;

  public void process(Exchange exchange) {
    producer.sendBodyAndHeaders(
        uri, exchange.getMessage().getBody(), exchange.getMessage().getHeaders());
  }
}
