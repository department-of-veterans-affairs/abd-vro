package gov.va.vro.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;

import java.util.Objects;
import java.util.Optional;

final class ProcessorUtils {

  @SuppressWarnings("unchecked")
  <I> I getInputBody(Exchange exchange, Class<I> inputBodyClass) {
    if (inputBodyClass == null) return (I) exchange.getIn().getBody();

    return exchange.getIn().getBody(inputBodyClass);
  }

  void conditionallySetOutputBody(Exchange exchange, Object body) {
    if (Objects.requireNonNull(exchange.getPattern()) == ExchangePattern.InOut) {
      if (body == null || (body instanceof Optional && ((Optional<?>) body).isEmpty())) return;
      exchange.getMessage().setBody(body);
    }
  }
}
