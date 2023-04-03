package gov.va.vro.camel.processor;

import org.apache.camel.Exchange;

import java.util.Optional;

final class ProcessorUtils {

  @SuppressWarnings("unchecked")
  static <I> I getInputBody(Exchange exchange, Class<I> inputBodyClass) {
    if (inputBodyClass == null) return (I) exchange.getIn().getBody();

    return exchange.getIn().getBody(inputBodyClass);
  }

  static void conditionallySetOutputBody(Exchange exchange, Object body) {
    switch (exchange.getPattern()) {
      case InOut -> exchange.getMessage().setBody(body);
      case InOptionalOut -> {
        if (body == null || (body instanceof Optional && ((Optional) body).isEmpty())) break;
        exchange.getMessage().setBody(body);
      }
    }
  }
}
